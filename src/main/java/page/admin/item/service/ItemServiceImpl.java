package page.admin.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.exception.ItemServiceException;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.LoginSessionInfo;
import page.admin.member.repository.MemberRepository;
import page.admin.utils.exception.DataNotFoundException;
import page.admin.utils.exception.FileProcessingException;
import page.admin.utils.file.FileStore;
import page.admin.item.domain.*;
import page.admin.item.repository.DeliveryCodeRepository;
import page.admin.item.repository.ItemRepository;
import page.admin.item.repository.ItemTypeRepository;
import page.admin.item.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * 제품 저장
     */
    @Override
    public Item saveItem(ItemSaveForm form, LoginSessionInfo sellerInfo) {
        UploadFile mainImage = fileStore.storeFile(form.getMainImage());
        List<UploadFile> thumbnails = fileStore.storeFiles(form.getThumbnails());

        List<Region> regions = regionRepository.findAllById(form.getRegions());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }

        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new DataNotFoundException("잘못된 ItemType ID입니다."));

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new DataNotFoundException("잘못된 DeliveryCode입니다."));

        Member seller = memberRepository.findById(sellerInfo.getUserNo())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 판매자입니다."));

        Item item = new Item(
                form.getItemName(), form.getPrice(), form.getQuantity(),
                form.getOpen(), regions, itemType, deliveryCode,
                mainImage, thumbnails, seller
        );

        return itemRepository.save(item);
    }

    /**
     * 전체 제품 조회
     */
    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * 페이징,검색,정렬 기능을
     * 포함한 제품 전체 조회
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        return itemRepository.findByItemNameContainingIgnoreCase(keyword, pageable);
    }

    /**
     * 제품 수정
     */
    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemServiceException("해당 아이템이 없습니다. ID: " + id));

        try {
            updateBasicDetails(item, form);
            processFiles(item, form);
            updateAssociations(item, form);
            itemRepository.save(item);
        } catch (FileProcessingException e) {
            log.error("파일 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("아이템 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new ItemServiceException("아이템 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 제품 삭제 (단일 상품)
     */
    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID입니다."));

        // 공통 삭제 로직 호출
        deleteItemInternal(item);

        // 데이터베이스에서 상품 삭제
        itemRepository.delete(item);
        log.info("상품 데이터 삭제 완료: {}", itemId);
    }

    /**
     * 제품 삭제 (여러 상품)
     */
    @Override
    @Transactional
    public void deleteItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findAllById(itemIds);

        if (items.size() != itemIds.size()) {
            throw new IllegalArgumentException("일부 상품이 존재하지 않습니다.");
        }

        // 모든 상품에 대해 삭제 로직 적용
        for (Item item : items) {
            deleteItemInternal(item);
        }

        // 데이터베이스에서 상품 삭제
        itemRepository.deleteAll(items);
        log.info("선택된 상품 데이터 삭제 완료: {}", itemIds);
    }

    /**
     * 상품 삭제를 위한 내부 공통 로직
     */
    private void deleteItemInternal(Item item) {
        // 메인 이미지 삭제
        if (item.getMainImage() != null) {
            boolean deleted = fileStore.deleteFile(item.getMainImage().getStoreFileName());
            if (!deleted) {
                log.warn("메인 이미지 삭제 실패 (파일 없음 또는 삭제 오류): {}", item.getMainImage().getStoreFileName());
            }
        }

        // 썸네일 삭제
        if (item.getThumbnails() != null) {
            for (UploadFile thumbnail : item.getThumbnails()) {
                boolean deleted = fileStore.deleteFile(thumbnail.getStoreFileName());
                if (!deleted) {
                    log.warn("썸네일 삭제 실패 (파일 없음 또는 삭제 오류): {}", thumbnail.getStoreFileName());
                }
            }
        }

        log.info("상품 파일 삭제 처리 완료: {}", item.getItemId());
    }




    /**
     * 제품 조회 (단일 조회)
     */
    @Override
    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
    }

    /**
     * 제품 상세 정보 반환
     */
    @Override
    public ItemViewForm getItemViewForm(Long id) {
        return toItemViewForm(getItem(id));
    }

    /**
     * 제품 수정 폼 데이터 반환
     */
    @Override
    public ItemEditForm getItemEditForm(Long id) {
        return toItemEditForm(getItem(id));
    }

    /**
     * 제품 업데이트용 데이터 반환
     */
    @Override
    public ItemUpdateForm getItemForUpdate(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
        return toItemUpdateForm(item);
    }

    // ======= Helper Methods =======

    private void updateBasicDetails(Item item, ItemUpdateForm form) {
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice().longValue());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());
        log.info("아이템 기본 정보 업데이트 완료: {}", item);
    }

    private void processFiles(Item item, ItemUpdateForm form) {
        if (form.getMainImage() != null && !form.getMainImage().isEmpty()) {
            UploadFile updatedMainImage = fileStore.replaceFile(item.getMainImage(), form.getMainImage());
            item.setMainImage(updatedMainImage);
            log.info("메인 이미지 업데이트 완료: {}", updatedMainImage);
        }

        if (form.getThumbnails() != null && !form.getThumbnails().isEmpty()) {
            List<UploadFile> updatedThumbnails = fileStore.replaceFiles(item.getThumbnails(), form.getThumbnails());
            item.setThumbnails(updatedThumbnails);
            log.info("썸네일 업데이트 완료: {}개", updatedThumbnails.size());
        }
    }

    private void updateAssociations(Item item, ItemUpdateForm form) {
        List<Region> regions = regionRepository.findByCodeIn(form.getRegionCodes());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }
        item.setRegions(regions);

        ItemType itemType = itemTypeRepository.findByCode(form.getItemType())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType 코드입니다."));
        item.setItemType(itemType);

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode 코드입니다."));
        item.setDeliveryCode(deliveryCode);

        log.info("연관 엔티티 업데이트 완료 - 지역: {}, 상품 종류: {}, 배송 방식: {}",
                item.getRegions(), item.getItemType(), item.getDeliveryCode());
    }

    private ItemViewForm toItemViewForm(Item item) {
        ItemViewForm dto = new ItemViewForm();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setPrice(item.getPrice());
        dto.setFormattedPrice(dto.getFormattedPrice()); // 가격 포맷
        dto.setQuantity(item.getQuantity());
        dto.setOpen(item.getOpen());
        dto.setRegions(item.getRegions());
        dto.setItemType(item.getItemType());
        dto.setDeliveryCode(item.getDeliveryCode());
        dto.setMainImage(item.getMainImage());
        dto.setThumbnails(item.getThumbnails());
        return dto;
    }

    private ItemEditForm toItemEditForm(Item item) {
        ItemEditForm dto = new ItemEditForm();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setOpen(item.getOpen());
        dto.setRegionCodes(convertRegionsToCodes(item.getRegions()));
        dto.setItemType(item.getItemType().getCode());
        dto.setDeliveryCode(item.getDeliveryCode().getCode());
        dto.setMainImage(item.getMainImage());
        dto.setThumbnails(item.getThumbnails());
        return dto;
    }

    private ItemUpdateForm toItemUpdateForm(Item item) {
        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(item.getItemId());
        form.setItemName(item.getItemName());
        form.setPrice(item.getPrice().intValue());
        form.setQuantity(item.getQuantity());
        form.setOpen(item.getOpen());
        form.setRegionCodes(item.getRegions().stream()
                .map(Region::getCode)
                .collect(Collectors.toList()));
        form.setItemType(item.getItemType().getCode());
        form.setDeliveryCode(item.getDeliveryCode().getCode());
        return form;
    }

    private List<String> convertRegionsToCodes(List<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return List.of();
        }
        return regions.stream()
                .map(Region::getCode)
                .collect(Collectors.toList());
    }
}
