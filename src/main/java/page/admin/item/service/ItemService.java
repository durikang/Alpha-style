package page.admin.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import page.admin.item.domain.Item;
import page.admin.item.domain.UploadFile;
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
    Page<Item> searchItems(String keyword, Pageable pageable);
    Item saveItem(ItemSaveForm form, LoginSessionInfo seller) throws IOException;
    Item getItem(Long id); // DTO로 반환
    ItemViewForm getItemViewForm(Long id); // DTO로 반환
    ItemEditForm getItemEditForm(Long id); // DTO로 반환
    ItemUpdateForm getItemForUpdate(Long id);
    void updateItem(Long id, ItemUpdateForm form); // 수정자 정보 제거
    List<Item> getAllItems();
    void deleteItem(Long id);
    void deleteItems(List<Long> itemIds);

}

