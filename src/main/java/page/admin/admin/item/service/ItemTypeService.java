package page.admin.admin.item.service;

import page.admin.admin.item.domain.ItemType;

import java.util.List;
import java.util.Optional;

public interface ItemTypeService {
    List<ItemType> findAll(); // 모든 상품 종류 조회

    void save(ItemType newItemType);

    Optional<ItemType> findById(Long id);

    void deleteById(Long id);
}
