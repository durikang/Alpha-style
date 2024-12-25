package page.admin.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.member.domain.dto.LoginSessionInfo;

import java.util.List;
import java.util.Set;

// ItemService.java
public interface ItemService {
    Page<Item> searchItems(String keyword, Pageable pageable);
    Item saveItem(ItemSaveForm form, LoginSessionInfo seller);
    Item getItem(Long id); // 단일 엔티티 반환
    ItemViewForm getItemViewForm(Long id); // DTO 반환
    ItemEditForm getItemEditForm(Long id); // DTO 반환
    void updateItem(Long id, ItemUpdateForm form);
    List<Item> getAllItems();
    void deleteItem(Long id);
    void deleteItems(List<Long> itemIds);
    Set<String> getItemRegions(Long itemId);
}


