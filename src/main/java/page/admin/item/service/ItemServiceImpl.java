package page.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import page.admin.item.domain.*;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.repository.*;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.LoginSessionInfo;
import page.admin.member.repository.MemberRepository;
import page.admin.utils.exception.DataNotFoundException;
import page.admin.utils.file.FileStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ItemServiceImpl
 *
 * 제품 관리 비즈니스 로직을 처리하는 ItemService 인터페이스의 구현체입니다.
 *
 * 주요 기능:
 * - 제품 저장 (파일 저장 및 연관 엔티티 설정 포함)
 * - 제품 수정 (파일 교체 및 연관 엔티티 업데이트 포함)
 * - 제품 삭제 (파일 삭제 및 데이터베이스 엔티티 삭제)
 * - 제품 조회 및 수정 폼 데이터 제공
 *
 * 사용 기술:
 * - Spring Data JPA를 이용한 데이터베이스 연동
 * - @Transactional을 활용한 트랜잭션 관리
 * - Lombok을 사용한 Boilerplate 코드 제거
 * - 커스텀 예외 처리 클래스 사용 (FileProcessingException, DataNotFoundException, ItemServiceException)
 *
 * 의존성:
 * - Repository 계층: ItemRepository, RegionRepository, ItemTypeRepository, DeliveryCodeRepository, MemberRepository
 * - 파일 처리: FileStore
 */
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

    /**
     * 제품 저장
     */
    @Override
    public Item saveItem(ItemSaveForm form, LoginSessionInfo sellerInfo) {
        UploadFile mainImage = fileStore.storeFile(form.getMainImage());
        Set<UploadFile> thumbnails = new HashSet<>();

        // 썸네일 4개 고정 처리
        List<MultipartFile> thumbnailFiles = form.getThumbnails().stream().limit(4).collect(Collectors.toList());
        for (int i = 0; i < 4; i++) {
            if (i < thumbnailFiles.size() && thumbnailFiles.get(i) != null && !thumbnailFiles.get(i).isEmpty()) {
                thumbnails.add(fileStore.storeFile(thumbnailFiles.get(i)));
            } else {
                // 빈 공간은 기본 Placeholder 이미지로 대체
                thumbnails.add(new UploadFile("https://via.placeholder.com/80x100.png?text=Thumbnail+" + (i + 1)));
            }
        }

        // 나머지 로직 동일
        Set<Region> regions = new HashSet<>(regionRepository.findAllById(form.getRegions()));
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }

        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new DataNotFoundException("잘못된 ItemType ID입니다."));

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new DataNotFoundException("잘못된 DeliveryCode입니다."));

        MainCategory mainCategory = mainCategoryRepository.findById(form.getMainCategory())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 메인 카테고리입니다."));

        SubCategory subCategory = subCategoryRepository.findById(form.getSubCategory())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 세부 카테고리입니다."));

        Member seller = memberRepository.findById(sellerInfo.getUserNo())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 판매자입니다."));

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


    /**
     * 제품 조회
     */
    @Override
    public Item getItem(Long id) {
        return itemRepository.findByIdWithThumbnails(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
    }

    /**
     * 제품 상세 정보 DTO 반환
     */
    @Override
    public ItemViewForm getItemViewForm(Long id) {
        Item item = itemRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
        return toItemViewForm(item);
    }

    /**
     * 제품 수정 폼 DTO 반환
     */
    @Override
    public ItemEditForm getItemEditForm(Long id) {
        Item item = getItem(id);
        return toItemEditForm(item);
    }

    /**
     * 제품 업데이트
     */
    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        updateBasicDetails(item, form);
        handleMainImageUpdate(item, form);
        handleThumbnailsUpdate(item, form);
        updateAssociations(item, form);

        itemRepository.save(item);
        log.info("아이템 업데이트 완료: {}", item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }


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


    /**
     * 제품 삭제
     */
    @Override
    public void deleteItem(Long id) {
        Item item = getItem(id);
        deleteItemInternal(item);
        itemRepository.delete(item);
    }

    /**
     * 다수 제품 삭제
     */
    @Override
    public void deleteItems(List<Long> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        items.forEach(this::deleteItemInternal);
        itemRepository.deleteAll(items);
    }



    /**
     * 제품 검색
     */
    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        return itemRepository.findByItemNameContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Helper: `toItemViewForm`
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
     * Helper: `toItemEditForm`
     */
    private ItemEditForm toItemEditForm(Item item) {
        ItemEditForm dto = new ItemEditForm();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setPurchasePrice(item.getPurchasePrice());
        dto.setSalePrice(item.getSalePrice());
        dto.setQuantity(item.getQuantity());
        dto.setOpen(item.getOpen());
        dto.setRegionCodes(item.getRegions().stream().map(Region::getCode).collect(Collectors.toSet()));
        dto.setItemType(item.getItemType().getId());
        dto.setDeliveryCode(item.getDeliveryCode().getCode());
        dto.setMainCategory(item.getMainCategory().getId());
        dto.setSubCategory(item.getSubCategory().getId());
        dto.setMainImage(item.getMainImage());

        dto.setThumbnails(item.getThumbnails());

        return dto;
    }

    /**
     * Helper: `toItemUpdateForm`
     */
    private ItemUpdateForm toItemUpdateForm(Item item) {
        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(item.getItemId());
        form.setItemName(item.getItemName());
        form.setPurchasePrice(item.getPurchasePrice());
        form.setSalePrice(item.getSalePrice());
        form.setQuantity(item.getQuantity());
        form.setOpen(item.getOpen());
        form.setRegionCodes(item.getRegions().stream().map(Region::getCode).collect(Collectors.toSet()));
        form.setItemType(item.getItemType().getId());
        form.setDeliveryCode(item.getDeliveryCode().getCode());
        form.setMainCategory(item.getMainCategory().getId());
        form.setSubCategory(item.getSubCategory().getId());
        return form;
    }

    private void handleMainImageUpdate(Item item, ItemUpdateForm form) {
        if (form.getMainImage() != null && !form.getMainImage().isEmpty()) {
            UploadFile updatedMainImage = fileStore.replaceFile(item.getMainImage(), form.getMainImage());
            item.setMainImage(updatedMainImage);
            log.info("메인 이미지 업데이트 완료: {}", updatedMainImage);
        }
    }
    private void handleThumbnailsUpdate(Item item, ItemUpdateForm form) {
        Set<UploadFile> updatedThumbnails = new HashSet<>();
        List<MultipartFile> newThumbnails = form.getThumbnails() != null
                ? form.getThumbnails().stream().limit(4).collect(Collectors.toList())
                : List.of();

        for (int i = 0; i < 4; i++) {
            if (i < newThumbnails.size() && newThumbnails.get(i) != null && !newThumbnails.get(i).isEmpty()) {
                // 새로운 썸네일 저장
                updatedThumbnails.add(fileStore.storeFile(newThumbnails.get(i)));
            } else if (i < form.getExistingThumbnails().size()) {
                // 기존 썸네일 유지
                updatedThumbnails.add(new UploadFile(form.getExistingThumbnails().get(i)));
            } else {
                // 빈 공간은 기본 Placeholder 이미지로 대체
                updatedThumbnails.add(new UploadFile("https://via.placeholder.com/80x100.png?text=Thumbnail+" + (i + 1)));
            }
        }

        item.setThumbnails(updatedThumbnails);
        log.info("썸네일 업데이트 완료: {}", updatedThumbnails.size());
    }

    private void updateBasicDetails(Item item, ItemUpdateForm form) {
        item.setItemName(form.getItemName());
        item.setPurchasePrice(form.getPurchasePrice());
        item.setSalePrice(form.getSalePrice());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());
        log.info("아이템 기본 정보 업데이트 완료: {}", item);
    }
    private void updateAssociations(Item item, ItemUpdateForm form) {
        Set<Region> regions = regionRepository.findByCodeIn(form.getRegionCodes());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }
        item.setRegions(regions);

        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType ID입니다."));
        item.setItemType(itemType);

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode 코드입니다."));
        item.setDeliveryCode(deliveryCode);

        MainCategory mainCategory = mainCategoryRepository.findById(form.getMainCategory())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메인 카테고리입니다."));
        item.setMainCategory(mainCategory);

        SubCategory subCategory = subCategoryRepository.findById(form.getSubCategory())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 세부 카테고리입니다."));
        item.setSubCategory(subCategory);

        log.info("연관 엔티티 업데이트 완료: 지역={}, 상품 종류={}, 배송 방식={}, 메인 카테고리={}, 세부 카테고리={}",
                item.getRegions(), item.getItemType(), item.getDeliveryCode(), item.getMainCategory(), item.getSubCategory());
    }
    private void deleteItemInternal(Item item) {
        if (item.getMainImage() != null) {
            boolean mainImageDeleted = fileStore.deleteFile(item.getMainImage().getStoreFileName());
            if (!mainImageDeleted) {
                log.warn("메인 이미지 삭제 실패: {}", item.getMainImage().getStoreFileName());
            }
        }

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
