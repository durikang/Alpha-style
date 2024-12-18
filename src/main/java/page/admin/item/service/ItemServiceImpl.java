package page.admin.item.service;

import org.springframework.context.annotation.Primary;
import page.admin.item.domain.dto.ItemDetailForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.member.domain.Member;
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

    @Override
    public Item saveItem(ItemSaveForm form, Member seller) throws IOException {
        UploadFile mainImage = fileStore.storeFile(form.getMainImage());
        List<UploadFile> thumbnails = fileStore.storeFiles(form.getThumbnails());

        // Region, ItemType, DeliveryCode 조회
        List<Region> regions = regionRepository.findAllById(form.getRegionIds());
        ItemType itemType = itemTypeRepository.findById(form.getItemTypeId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType ID입니다."));
        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode입니다."));

        // Item 객체 생성 (판매자 추가)
        Item item = new Item(
                form.getItemName(), form.getPrice(), form.getQuantity(),
                form.getOpen(), regions, itemType, deliveryCode,
                mainImage, thumbnails, seller // 판매자 추가
        );

        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        // 아이템 조회
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));

        // 상품 정보 업데이트
        updateItemDetails(item, form);

        // 이미지 처리
        handleImages(item, form);

        // 엔티티 저장
        itemRepository.save(item);
    }

    // 상품 정보 업데이트
    private void updateItemDetails(Item item, ItemUpdateForm form) {
        // 기본 정보 업데이트
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice().longValue());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());

        // 지역 코드로 Region 엔티티 리스트 조회
        List<String> regionCodes = form.getRegions();
        List<Region> regions = regionRepository.findByCodeIn(regionCodes);

        // 상품 종류 코드로 ItemType 엔티티 조회
        ItemType itemType = itemTypeRepository.findByCode(form.getItemType())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType 코드입니다."));

        // 배송 방식 코드로 DeliveryCode 엔티티 조회
        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode 코드입니다."));

        // 연관 데이터 설정
        item.setRegions(regions);
        item.setItemType(itemType);
        item.setDeliveryCode(deliveryCode);
    }


    // 이미지 처리
    private void handleImages(Item item, ItemUpdateForm form) {
        try {
            if (form.getMainImage() != null && !form.getMainImage().isEmpty()) {
                UploadFile mainImage = fileStore.storeFile(form.getMainImage());
                item.setMainImage(mainImage);
            }

            if (form.getThumbnails() != null && !form.getThumbnails().isEmpty()) {
                List<UploadFile> thumbnails = form.getThumbnails().stream()
                        .map(file -> {
                            try {
                                return fileStore.storeFile(file);
                            } catch (IOException e) {
                                throw new RuntimeException("썸네일 업로드 실패", e);
                            }
                        })
                        .collect(Collectors.toList());
                item.setThumbnails(thumbnails);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
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
        form.setId(item.getItemId());
        form.setItemName(item.getItemName());
        form.setPrice(item.getPrice().intValue());
        form.setQuantity(item.getQuantity());
        form.setOpen(item.getOpen());
        form.setRegions(item.getRegions().stream()
                .map(Region::getCode) // Region 객체에서 코드만 추출
                .collect(Collectors.toList()));
        form.setItemType(item.getItemType().getCode()); // ItemType 객체에서 코드만 추출
        form.setDeliveryCode(item.getDeliveryCode().getCode()); // DeliveryCode 객체에서 코드만 추출
        return form;
    }



    @Override
    public ItemDetailForm getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
        return toItemDetailForm(item);
    }

    private ItemDetailForm toItemDetailForm(Item item) {
        ItemDetailForm dto = new ItemDetailForm();
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
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
