package page.admin.item.service;

import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.LoginSessionInfo;

import java.io.IOException;
import java.util.List;

// ItemService.java
public interface ItemService {
    Item saveItem(ItemSaveForm form, LoginSessionInfo seller) throws IOException;
    Item getItem(Long id); // DTO로 반환
    ItemViewForm getItemViewForm(Long id); // DTO로 반환
    ItemEditForm getItemEditForm(Long id); // DTO로 반환
    ItemUpdateForm getItemForUpdate(Long id);
    void updateItem(Long id, ItemUpdateForm form); // 수정자 정보 제거
    List<Item> getAllItems();
    void deleteItem(Long id);
}

