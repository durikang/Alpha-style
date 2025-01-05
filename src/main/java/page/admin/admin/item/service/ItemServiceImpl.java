package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.*;
import page.admin.admin.item.domain.dto.ItemEditForm;
import page.admin.admin.item.domain.dto.ItemSaveForm;
import page.admin.admin.item.domain.dto.ItemUpdateForm;
import page.admin.admin.item.domain.dto.ItemViewForm;
import page.admin.admin.item.repository.*;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.exception.DataNotFoundException;
import page.admin.common.utils.file.FileStore;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final RegionRepository regionRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final DeliveryCodeRepository deliveryCodeRepository;
    private final FileStore fileStore;
    private final MemberRepository memberRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    // ======================================
    // 1) 상품 등록
    // ======================================
    @Override
    public Item saveItem(ItemSaveForm form, LoginSessionInfo sellerInfo) {

        // (1) 메인 이미지
        UploadFile mainImage = fileStore.storeFile(form.getMainImage());

        // (2) 썸네일 처리 (최대 4개)
        Set<UploadFile> thumbnails = new HashSet<>();
        List<MultipartFile> thumbnailFiles = form.getThumbnails().stream()
                .limit(4)
                .collect(Collectors.toList());
        for (int i = 0; i < 4; i++) {
            if (i < thumbnailFiles.size() && thumbnailFiles.get(i) != null && !thumbnailFiles.get(i).isEmpty()) {
                thumbnails.add(fileStore.storeFile(thumbnailFiles.get(i)));
            } else {
                // Placeholder 이미지로 채우기
                thumbnails.add(new UploadFile("https://via.placeholder.com/80x100.png?text=Thumbnail+" + (i + 1)));
            }
        }

        // (3) 지역 -> regionCodes
        Set<Region> regions = regionRepository.findByCodeIn(form.getRegionCodes());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }

        // (4) 기타 연관 엔티티
        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new DataNotFoundException("잘못된 ItemType ID입니다."));

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new DataNotFoundException("잘못된 DeliveryCode입니다."));

        MainCategory mainCategory = mainCategoryRepository.findById(form.getMainCategory())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 메인 카테고리입니다."));

        SubCategory subCategory = subCategoryRepository.findById(form.getSubCategory())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 세부 카테고리입니다."));

        Member seller = memberRepository.findById(sellerInfo.getUserNo())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 판매자(회원)입니다."));

        // (5) 엔티티 생성 & 저장
        Item item = new Item(
                form.getItemName(),
                form.getPurchasePrice(),
                form.getSalePrice(),
                form.getQuantity(),
                form.getOpen(),
                regions,
                itemType,
                deliveryCode,
                mainImage,
                thumbnails,
                mainCategory,
                subCategory,
                seller
        );

        return itemRepository.save(item);
    }


    // ======================================
    // 2) 단일 엔티티 조회
    // ======================================
    @Override
    public Item getItem(Long id) {
        return itemRepository.findByIdWithThumbnails(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
    }

    // ======================================
    // 3) 상세 정보 DTO (ItemViewForm)
    // ======================================
    @Override
    public ItemViewForm getItemViewForm(Long id) {
        Item item = itemRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
        return toItemViewForm(item);
    }

    // ======================================
    // 4) 수정 폼 DTO (ItemEditForm)
    // ======================================
    @Override
    public ItemEditForm getItemEditForm(Long id) {
        Item item = itemRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        return toItemEditForm(item);
    }


    // ======================================
    // 5) 수정 로직 (ItemUpdateForm)
    // ======================================
    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        // (1) 기존 아이템 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        // (2) 기본 정보 업데이트
        updateBasicDetails(item, form);

        // (3) 메인 이미지 교체
        handleMainImageUpdate(item, form);

        // (4) 썸네일 교체
        handleThumbnailsUpdate(item, form);

        // (5) 연관 엔티티 업데이트 (지역, 카테고리, 배송 등)
        updateAssociations(item, form);

        itemRepository.save(item);
        log.info("아이템 업데이트 완료: {}", item);
    }

    // ======================================
    // 6) 전체 목록
    // ======================================
    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ======================================
    // 7) 지역 코드 반환
    // ======================================
    @Override
    public Set<String> getItemRegions(Long itemId) {
        Item item = itemRepository.findByIdWithRegions(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + itemId));

        if (item.getRegions() == null || item.getRegions().isEmpty()) {
            return Set.of();
        }

        return item.getRegions().stream()
                .map(Region::getCode)
                .collect(Collectors.toSet());
    }

    // ======================================
    // 8) 단일 삭제
    // ======================================
    @Override
    public void deleteItem(Long id) {
        Item item = getItem(id); // 내부적으로 findByIdWithThumbnails
        deleteItemInternal(item);
        itemRepository.delete(item);
    }

    // ======================================
    // 9) 다수 삭제
    // ======================================
    @Override
    public void deleteItems(List<Long> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        items.forEach(this::deleteItemInternal);
        itemRepository.deleteAll(items);
    }

    // ======================================
    // 10) 검색
    // ======================================
    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        return itemRepository.findByItemNameContainingIgnoreCase(keyword, pageable);
    }

    // ----------------------------------------------------------------------
    // 아래는 내부 helper 메서드들
    // ----------------------------------------------------------------------

    /**
     * Item -> ItemViewForm 변환
     */
    private ItemViewForm toItemViewForm(Item item) {
        return new ItemViewForm(
                item.getItemId(),
                item.getItemName(),
                item.getPurchasePrice(),
                item.getSalePrice(),
                item.getQuantity(),
                item.getOpen(),
                item.getRegions(),
                item.getItemType(),
                item.getDeliveryCode(),
                item.getMainCategory(),
                item.getSubCategory(),
                item.getMainImage(),
                item.getThumbnails() != null ? item.getThumbnails() : Set.of()
        );
    }

    /**
     * Item -> ItemEditForm 변환
     */
    private ItemEditForm toItemEditForm(Item item) {
        ItemEditForm dto = new ItemEditForm();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setPurchasePrice(item.getPurchasePrice());
        dto.setSalePrice(item.getSalePrice());
        dto.setQuantity(item.getQuantity());
        dto.setOpen(item.getOpen());

        // (1) 지역
        if (item.getRegions() != null) {
            // regionCodes가 Set<String> 이라면, Region::getCode를 매핑
            Set<String> regionCodes = item.getRegions().stream()
                    .map(Region::getCode)
                    .collect(Collectors.toSet());
            dto.setRegionCodes(regionCodes);
        }

        // (2) 상품 타입
        if (item.getItemType() != null) {
            dto.setItemType(item.getItemType().getId());
        }

        // (3) 배송 방식
        if (item.getDeliveryCode() != null) {
            dto.setDeliveryCode(item.getDeliveryCode().getCode());
        }

        // (4) 메인 카테고리
        if (item.getMainCategory() != null) {
            dto.setMainCategory(item.getMainCategory().getId());
        }

        // (5) 서브 카테고리
        if (item.getSubCategory() != null) {
            dto.setSubCategory(item.getSubCategory().getId());
        }

        // [이미지 초기화]
        if (item.getMainImage() != null) {
            dto.setMainImagePath(item.getMainImage().getStoreFileName());
        }
        if (item.getThumbnails() != null) {
            List<String> thumbnailPaths = item.getThumbnails().stream()
                    .map(UploadFile::getStoreFileName)
                    .collect(Collectors.toList());
            dto.setThumbnailPaths(thumbnailPaths);
        }

        return dto;
    }


    // 만약 Update 시, 기존 썸네일 유지/삭제 로직 처리를 위한 `toItemUpdateForm`이 필요하다면 추가
    // 생략 가능

    // ---------------------------------------
    // updateItem() 내 세부 메서드
    // ---------------------------------------
    private void updateBasicDetails(Item item, ItemUpdateForm form) {
        item.setItemName(form.getItemName());
        item.setPurchasePrice(form.getPurchasePrice());
        item.setSalePrice(form.getSalePrice());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());
        log.info("아이템 기본 정보 업데이트 완료: {}", item);
    }

    private void handleMainImageUpdate(Item item, ItemUpdateForm form) {
        if (form.getMainImage() != null && !form.getMainImage().isEmpty()) {
            UploadFile updatedMainImage = fileStore.replaceFile(item.getMainImage(), form.getMainImage());
            item.setMainImage(updatedMainImage);
            log.info("메인 이미지 업데이트 완료: {}", updatedMainImage);
        }
    }

    private void handleThumbnailsUpdate(Item item, ItemUpdateForm form) {
        // 로그 추가: 기존 썸네일 확인
        log.debug("Handling thumbnails update. ExistingThumbnails: {}", form.getExistingThumbnails());

        // 1) 새로 업로드/기존유지 등을 바탕으로 `updatedThumbnails`를 만든다 (UploadFile들의 Set)
        Set<UploadFile> updatedThumbnails = new HashSet<>();
        List<MultipartFile> newThumbnails = (form.getThumbnails() != null)
                ? form.getThumbnails().stream().limit(4).collect(Collectors.toList())
                : List.of();

        List<String> existingThumbnails = form.getExistingThumbnails();
        if (existingThumbnails != null) {
            log.debug("Number of existing thumbnails: {}", existingThumbnails.size());
        }

        for (int i = 0; i < 4; i++) {
            if (i < newThumbnails.size()
                    && newThumbnails.get(i) != null
                    && !newThumbnails.get(i).isEmpty()) {
                // 새로운 썸네일 저장
                UploadFile newFile = fileStore.storeFile(newThumbnails.get(i));
                updatedThumbnails.add(newFile);
                log.debug("Added new thumbnail: {}", newFile.getStoreFileName());
            } else if (existingThumbnails != null
                    && i < existingThumbnails.size()
                    && existingThumbnails.get(i) != null
                    && !existingThumbnails.get(i).isEmpty()
                    && !existingThumbnails.get(i).startsWith("https://")
                    && !existingThumbnails.get(i).startsWith("http://")) {
                // 기존 썸네일 유지 (URL이 아닌 경우만)
                String existingPath = existingThumbnails.get(i);
                updatedThumbnails.add(new UploadFile(existingPath));
                log.debug("Maintained existing thumbnail: {}", existingPath);
            } else {
                // 빈 공간은 플레이스홀더 URL 사용 (UploadFile로 추가하지 않음)
                // 플레이스홀더는 템플릿에서 직접 설정
                // 따라서, 이 부분에서는 아무것도 추가하지 않음
                log.debug("No thumbnail available for index {}. Using placeholder.", i + 1);
            }
        }

        // 2) 기존 컬렉션을 clear하고 updatedThumbnails를 추가
        Set<UploadFile> originalThumbnails = item.getThumbnails(); // 원본 컬렉션
        originalThumbnails.clear();             // 기존 엔티티들 제거 -> orphan으로 인식 -> DB에서 삭제됨
        originalThumbnails.addAll(updatedThumbnails); // 새 엔티티들 추가 -> DB에 INSERT됨

        log.info("썸네일 업데이트 완료: {} thumbnails updated.", updatedThumbnails.size());
    }



    private void updateAssociations(Item item, ItemUpdateForm form) {
        // 1) 지역
        Set<Region> regions = regionRepository.findByCodeIn(form.getRegionCodes());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }
        item.setRegions(regions);

        // 2) 상품타입
        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType ID입니다."));
        item.setItemType(itemType);

        // 3) 배송코드
        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode 코드입니다."));
        item.setDeliveryCode(deliveryCode);

        // 4) 카테고리
        MainCategory mainCategory = mainCategoryRepository.findById(form.getMainCategory())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메인 카테고리입니다."));
        item.setMainCategory(mainCategory);

        SubCategory subCategory = subCategoryRepository.findById(form.getSubCategory())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 세부 카테고리입니다."));
        item.setSubCategory(subCategory);

        log.info("연관 엔티티 업데이트 완료: 지역={}, 상품 종류={}, 배송 방식={}, 메인 카테고리={}, 세부 카테고리={}",
                item.getRegions(), item.getItemType(), item.getDeliveryCode(),
                item.getMainCategory(), item.getSubCategory());
    }

    // ---------------------------------------
    // 삭제 시 첨부 파일 제거
    // ---------------------------------------
    private void deleteItemInternal(Item item) {
        // 메인 이미지 삭제
        if (item.getMainImage() != null) {
            boolean mainImageDeleted = fileStore.deleteFile(item.getMainImage().getStoreFileName());
            if (!mainImageDeleted) {
                log.warn("메인 이미지 삭제 실패: {}", item.getMainImage().getStoreFileName());
            }
        }

        // 썸네일 삭제
        if (item.getThumbnails() != null) {
            for (UploadFile thumbnail : item.getThumbnails()) {
                boolean thumbnailDeleted = fileStore.deleteFile(thumbnail.getStoreFileName());
                if (!thumbnailDeleted) {
                    log.warn("썸네일 삭제 실패: {}", thumbnail.getStoreFileName());
                }
            }
        }

        log.info("아이템 내부 파일 삭제 완료: {}", item.getItemId());
    }
}
