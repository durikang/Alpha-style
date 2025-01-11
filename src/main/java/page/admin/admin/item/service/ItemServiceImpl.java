package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.*;
import page.admin.admin.item.domain.dto.*;
import page.admin.admin.item.repository.*;
import page.admin.admin.manager.domain.dto.CategoryWithItemsDTO;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.exception.DataNotFoundException;
import page.admin.common.utils.file.FileStore;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository; // 리뷰 리포지토리 추가
    private final ReviewService reviewService; // 리뷰 서비스 추가
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
        Set<UploadFile> thumbnails = form.getThumbnails().stream()
                .limit(4)
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> fileStore.storeFile(file))
                .collect(Collectors.toSet());

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

        // 메인 이미지와 Item 연관 설정
        mainImage.setItem(item);

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
        ItemViewForm form = toItemViewForm(item);
        Double averageRating = getAverageRating(id);
        form.setAverageRating(averageRating);
        return form;
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
        // 필수 필드 유효성 검증
        if (form.getItemName() == null || form.getItemName().isBlank()) {
            throw new IllegalArgumentException("필수 필드가 누락되었습니다.");
        }

        // (1) 기존 아이템 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        // (2) 기본 정보 업데이트
        updateBasicDetails(item, form);

        // (3) 메인 이미지 교체
        handleMainImageUpdate(item, form);

        // (4) 썸네일 교체
        handleThumbnailsUpdate(item, form);

        // (5) 기타 연관 관계 업데이트
        updateAssociations(item, form);

        // (6) 저장
        itemRepository.save(item);
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
    // 8) 인기상품 조회
    // ======================================
    @Override
    @Transactional(readOnly = true)
    public List<Item> getPopularItems(Pageable pageable) {
        // 인기상품 기준: 조회수 DESC, 판매량 DESC, 평균 평점 DESC
        return itemRepository.findPopularItems(pageable);
    }

    // ======================================
    // 9) 조회수 증가
    // ======================================
    @Override
    @Transactional
    public void incrementViewCount(Long itemId) {
        Item item = getItem(itemId);
        item.setViewCount(item.getViewCount() + 1);
        itemRepository.save(item);
        log.info("조회수 증가: Item ID={}, New View Count={}", itemId, item.getViewCount());
    }

    // ======================================
    // 10) 평균 평점 계산
    // ======================================
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long itemId) {
        Page<ReviewDTO> reviews = reviewService.getReviewsByItemId(itemId, Pageable.unpaged());
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(ReviewDTO::getRating)
                .average()
                .orElse(0.0);
    }


    // ======================================
    // 11) 메인 카테고리별로 최대 4개의 상품 조회
    // ======================================
    @Override
    @Transactional(readOnly = true)
    public List<CategoryWithItemsDTO> getItemsGroupedByMainCategory(int limitPerCategory) {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        List<CategoryWithItemsDTO> result = new ArrayList<>();

        for (MainCategory mainCategory : mainCategories) {
            Pageable pageable = PageRequest.of(0, limitPerCategory, Sort.by("salePrice").descending());
            List<Item> items = itemRepository.findTop4ByMainCategoryIdAndOpenTrueOrderBySalePriceDesc(mainCategory.getId());
            if (!items.isEmpty()) {
                result.add(new CategoryWithItemsDTO(mainCategory, items));
            }
        }

        return result;
    }

    // ======================================
    // 12) 상품 검색
    // ======================================
    @Override
    @Transactional(readOnly = true)
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        return itemRepository.searchItems(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findItemsByMainCategoryWithOffset(Long mainCategoryId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("salePrice").descending());
        return itemRepository.findByMainCategoryIdAndOpenTrue(mainCategoryId, pageable).getContent();
    }

    // ======================================
    // 13) 리뷰 관련 메서드 추가
    // ======================================
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByItemId(Long itemId, Pageable pageable) {
        return reviewRepository.findByItemItemId(itemId, pageable);
    }

    // ---------------------------------------
    // 삭제 시 첨부 파일 제거
    // ---------------------------------------
    @Override
    public void deleteItem(Long id) {
        Item item = getItem(id); // 내부적으로 findByIdWithThumbnails
        deleteItemInternal(item);
        itemRepository.delete(item);
    }

    @Override
    public void deleteItems(List<Long> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        items.forEach(this::deleteItemInternal);
        itemRepository.deleteAll(items);
    }

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
            item.getThumbnails().clear();
        }

        // 리뷰 삭제
        if (item.getReviews() != null && !item.getReviews().isEmpty()) {
            reviewRepository.deleteByItemItemId(item.getItemId());
            log.info("아이템 리뷰 삭제 완료: Item ID={}", item.getItemId());
        }

        log.info("아이템 내부 파일 삭제 완료: {}", item.getItemId());
    }

    // ---------------------------------------
    // 내부 helper 메서드들
    // ---------------------------------------

    /**
     * Item -> ItemViewForm 변환
     */
    public ItemViewForm toItemViewForm(Item item) {
        int thumbnailCount = item.getThumbnails() != null ? item.getThumbnails().size() : 0;
        log.debug("Item ID {} has {} thumbnails.", item.getItemId(), thumbnailCount);
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
                item.getThumbnails() != null ? new ArrayList<>(item.getThumbnails()) : List.<UploadFile>of(),
                item.getViewCount(),
                item.getSalesCount(),
                null // averageRating set in getItemViewForm
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

    private void updateBasicDetails(Item item, ItemUpdateForm form) {
        item.setItemName(form.getItemName());
        item.setPurchasePrice(form.getPurchasePrice());
        item.setSalePrice(form.getSalePrice());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());
    }

    private void handleMainImageUpdate(Item item, ItemUpdateForm form) {
        MultipartFile newMainImage = form.getNewMainImage();
        if (newMainImage != null && !newMainImage.isEmpty()) {
            // 기존 메인 이미지 삭제
            if (item.getMainImage() != null) {
                boolean mainImageDeleted = fileStore.deleteFile(item.getMainImage().getStoreFileName());
                if (!mainImageDeleted) {
                    log.warn("메인 이미지 삭제 실패: {}", item.getMainImage().getStoreFileName());
                }
            }
            // 새로운 메인 이미지 저장
            UploadFile updatedMainImage = fileStore.storeFile(newMainImage);
            item.setMainImage(updatedMainImage);
        }
    }

    private void handleThumbnailsUpdate(Item item, ItemUpdateForm form) {
        List<MultipartFile> newThumbnailFiles = form.getThumbnails();
        if (newThumbnailFiles != null && !newThumbnailFiles.isEmpty()) {
            // 기존 썸네일 삭제
            Set<UploadFile> currentThumbnails = item.getThumbnails();
            if (currentThumbnails != null) {
                Iterator<UploadFile> iterator = currentThumbnails.iterator();
                while (iterator.hasNext()) {
                    UploadFile thumbnail = iterator.next();
                    boolean deleted = fileStore.deleteFile(thumbnail.getStoreFileName());
                    if (!deleted) {
                        log.warn("썸네일 삭제 실패: {}", thumbnail.getStoreFileName());
                    }
                    iterator.remove(); // Hibernate 추적을 위해 iterator 사용
                }
            }

            // 새로운 썸네일 저장 및 연관관계 설정
            for (MultipartFile file : newThumbnailFiles) {
                if (file != null && !file.isEmpty()) {
                    UploadFile newThumbnail = fileStore.storeFile(file);
                    newThumbnail.setItem(item); // 연관관계 설정
                    item.getThumbnails().add(newThumbnail); // 기존 컬렉션에 추가
                }
            }
        }
    }







    private void updateAssociations(Item item, ItemUpdateForm form) {
        // 지역 업데이트
        if (form.getRegionCodes() != null && !form.getRegionCodes().isEmpty()) {
            Set<Region> updatedRegions = regionRepository.findByCodeIn(form.getRegionCodes());
            if (!updatedRegions.isEmpty()) {
                item.setRegions(updatedRegions);
            } else {
                throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
            }
        }

        // 상품 타입 업데이트
        if (form.getItemType() != null) {
            ItemType updatedItemType = itemTypeRepository.findById(form.getItemType())
                    .orElseThrow(() -> new DataNotFoundException("잘못된 ItemType ID입니다."));
            item.setItemType(updatedItemType);
        }

        // 배송 방식 업데이트
        if (form.getDeliveryCode() != null) {
            DeliveryCode updatedDeliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                    .orElseThrow(() -> new DataNotFoundException("잘못된 DeliveryCode입니다."));
            item.setDeliveryCode(updatedDeliveryCode);
        }

        // 메인 카테고리 업데이트
        if (form.getMainCategory() != null) {
            MainCategory updatedMainCategory = mainCategoryRepository.findById(form.getMainCategory())
                    .orElseThrow(() -> new DataNotFoundException("유효하지 않은 메인 카테고리입니다."));
            item.setMainCategory(updatedMainCategory);
        }

        // 서브 카테고리 업데이트
        if (form.getSubCategory() != null) {
            SubCategory updatedSubCategory = subCategoryRepository.findById(form.getSubCategory())
                    .orElseThrow(() -> new DataNotFoundException("유효하지 않은 세부 카테고리입니다."));
            item.setSubCategory(updatedSubCategory);
        }
    }
}
