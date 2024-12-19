package page.admin.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import page.admin.exception.ItemUpdateException;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
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

import java.io.IOException;
import java.util.List;
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

    @Override
    public Item saveItem(ItemSaveForm form, LoginSessionInfo sellerInfo) {
        UploadFile mainImage;
        List<UploadFile> thumbnails;

        try {
            // 파일 저장 처리
            mainImage = fileStore.storeFile(form.getMainImage());
            thumbnails = fileStore.storeFiles(form.getThumbnails());
        } catch (IOException e) {
            throw new FileProcessingException("파일 저장 중 오류가 발생했습니다.", e);
        }

        // 데이터 조회
        List<Region> regions = regionRepository.findAllById(form.getRegions());
        if (regions.isEmpty()) {
            throw new DataNotFoundException("유효한 지역 정보가 없습니다.");
        }

        ItemType itemType = itemTypeRepository.findById(form.getItemType())
                .orElseThrow(() -> new DataNotFoundException("잘못된 ItemType ID입니다."));

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new DataNotFoundException("잘못된 DeliveryCode입니다."));

        // `sellerInfo`의 userNo를 이용해 `Member` 조회
        Member seller = memberRepository.findById(sellerInfo.getUserNo())
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 판매자입니다."));

        // Item 생성
        Item item = new Item(
                form.getItemName(), form.getPrice(), form.getQuantity(),
                form.getOpen(), regions, itemType, deliveryCode,
                mainImage, thumbnails, seller
        );

        return itemRepository.save(item);
    }



    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        log.info("아이템 업데이트 시작 - ID: {}", id);

        // 아이템 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("해당 아이템이 없습니다. ID: {}", id);
                    return new ItemUpdateException("해당 아이템이 없습니다. ID: " + id);
                });

        log.info("아이템 조회 완료 - ID: {}", id);

        // 상품 정보 업데이트
        try {
            updateItemDetails(item, form);
            log.info("상품 정보 업데이트 완료 - ID: {}", id);
        } catch (Exception e) {
            log.error("상품 정보 업데이트 중 오류 발생 - ID: {}", id, e);
            throw new ItemUpdateException("상품 정보를 업데이트하는 중 오류가 발생했습니다.", e);
        }

        // 파일 처리 로직
        try {
            handleFiles(item, form);
            log.info("파일 처리 완료 - ID: {}", id);
        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생 - ID: {}", id, e);
            throw new FileProcessingException("파일 처리 중 오류가 발생했습니다.", e);
        }

        // 엔티티 저장
        try {
            itemRepository.save(item);
            log.info("아이템 저장 완료 - ID: {}", id);
        } catch (Exception e) {
            log.error("아이템 저장 중 오류 발생 - ID: {}", id, e);
            throw new ItemUpdateException("아이템 저장 중 오류가 발생했습니다.", e);
        }
    }


    // 상품 정보 업데이트
    private void updateItemDetails(Item item, ItemUpdateForm form) {
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice().longValue());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());

        // 연관 엔티티 업데이트
        List<Region> regions = regionRepository.findByCodeIn(form.getRegionCodes());
        item.setRegions(regions);

        ItemType itemType = itemTypeRepository.findByCode(form.getItemType())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType 코드입니다."));
        item.setItemType(itemType);

        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode 코드입니다."));
        item.setDeliveryCode(deliveryCode);
    }


    private void handleFiles(Item item, ItemUpdateForm form) {
        // 메인 이미지 처리
        if (form.getMainImage() != null && !form.getMainImage().isEmpty()) {
            if (item.getMainImage() != null) {
                try {
                    fileStore.deleteFile(item.getMainImage().getStoreFileName());
                    log.info("기존 메인 이미지 삭제 완료: {}", item.getMainImage().getStoreFileName());
                } catch (Exception e) {
                    log.error("기존 메인 이미지를 삭제하는 중 오류 발생: {}", item.getMainImage().getStoreFileName(), e);
                    throw new FileProcessingException("기존 메인 이미지를 삭제하는 중 오류가 발생했습니다.", e);
                }
            }
            try {
                UploadFile mainImage = fileStore.storeFile(form.getMainImage()); // MultipartFile 처리
                item.setMainImage(mainImage); // UploadFile로 설정
                log.info("새 메인 이미지 저장 완료: {}", mainImage.getStoreFileName());
            } catch (IOException e) {
                log.error("새 메인 이미지를 저장하는 중 오류 발생", e);
                throw new FileProcessingException("새 메인 이미지를 저장하는 중 오류가 발생했습니다.", e);
            }
        }

        // 썸네일 처리
        if (form.getThumbnails() != null && !form.getThumbnails().isEmpty()) {
            if (item.getThumbnails() != null && !item.getThumbnails().isEmpty()) {
                for (UploadFile thumbnail : item.getThumbnails()) {
                    try {
                        fileStore.deleteFile(thumbnail.getStoreFileName());
                        log.info("기존 썸네일 삭제 완료: {}", thumbnail.getStoreFileName());
                    } catch (Exception e) {
                        log.error("기존 썸네일을 삭제하는 중 오류 발생: {}", thumbnail.getStoreFileName(), e);
                        throw new FileProcessingException("기존 썸네일을 삭제하는 중 오류가 발생했습니다.", e);
                    }
                }
            }
            try {
                List<UploadFile> thumbnails = fileStore.storeFiles(form.getThumbnails()); // MultipartFile 목록 처리
                item.setThumbnails(thumbnails); // UploadFile 목록 설정
                log.info("새 썸네일 저장 완료: {}개", thumbnails.size());
            } catch (IOException e) {
                log.error("새 썸네일을 저장하는 중 오류 발생", e);
                throw new FileProcessingException("새 썸네일을 저장하는 중 오류가 발생했습니다.", e);
            }
        }
    }






    @Override
    public ItemUpdateForm getItemForUpdate(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
        return toItemUpdateForm(item);
    }

    private ItemUpdateForm toItemUpdateForm(Item item) {
        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(item.getItemId());
        form.setItemName(item.getItemName());
        form.setPrice(item.getPrice().intValue());
        form.setQuantity(item.getQuantity());
        form.setOpen(item.getOpen());
        form.setRegionCodes(item.getRegions().stream()
                .map(Region::getCode) // Region 객체에서 코드만 추출
                .collect(Collectors.toList()));
        form.setItemType(item.getItemType().getCode()); // ItemType 객체에서 코드만 추출
        form.setDeliveryCode(item.getDeliveryCode().getCode()); // DeliveryCode 객체에서 코드만 추출
        return form;
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
    }

    @Override
    public ItemViewForm getItemViewForm(Long id) {
        return toItemViewForm(getItem(id));
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

    @Override
    public ItemEditForm getItemEditForm(Long id) {
        return toItemEditForm(getItem(id));
    }

    private ItemEditForm toItemEditForm(Item item) {
        ItemEditForm dto = new ItemEditForm();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setOpen(item.getOpen());
        dto.setRegionCodes (convertRegionsToCodes(item.getRegions()));
        dto.setItemType(item.getItemType().getCode());
        dto.setDeliveryCode(item.getDeliveryCode().getCode());
        dto.setMainImage(item.getMainImage());
        dto.setThumbnails(item.getThumbnails());
        return dto;
    }
    private List<String> convertRegionsToCodes(List<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        return regions.stream()
                .map(Region::getCode) // 각 Region의 code 필드 추출
                .toList();
    }

    @Override
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        // 파일 삭제
        try {
            if (item.getMainImage() != null) {
                fileStore.deleteFile(item.getMainImage().getStoreFileName());
            }
            if (item.getThumbnails() != null) {
                for (UploadFile thumbnail : item.getThumbnails()) {
                    fileStore.deleteFile(thumbnail.getStoreFileName());
                }
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생", e);
        }

        itemRepository.delete(item);
    }
}
