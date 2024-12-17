package page.admin.item.service;

import org.springframework.context.annotation.Primary;
import page.admin.item.domain.dto.ItemSaveForm;
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
    public Item saveItem(ItemSaveForm form) throws IOException {
        UploadFile mainImage = fileStore.storeFile(form.getMainImage());
        List<UploadFile> thumbnails = fileStore.storeFiles(form.getThumbnails());

        // Region, ItemType, DeliveryCode 조회
        List<Region> regions = regionRepository.findAllById(form.getRegionIds());
        ItemType itemType = itemTypeRepository.findById(form.getItemTypeId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ItemType ID입니다."));
        DeliveryCode deliveryCode = deliveryCodeRepository.findByCode(form.getDeliveryCode())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 DeliveryCode입니다."));

        // Item 객체 생성
        Item item = new Item(
                form.getItemName(), form.getPrice(), form.getQuantity(),
                form.getOpen(), regions, itemType, deliveryCode,
                mainImage, thumbnails
        );

        return itemRepository.save(item);
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + id));
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public void updateItem(Long id, Item updateParam) {
        Item existingItem = getItem(id);
        existingItem.setItemName(updateParam.getItemName());
        existingItem.setPrice(updateParam.getPrice());
        existingItem.setQuantity(updateParam.getQuantity());
        existingItem.setRegions(updateParam.getRegions());
        existingItem.setItemType(updateParam.getItemType());
        existingItem.setDeliveryCode(updateParam.getDeliveryCode());
        existingItem.setMainImage(updateParam.getMainImage());
        existingItem.setThumbnails(updateParam.getThumbnails());
        itemRepository.save(existingItem);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
