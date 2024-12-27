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

public interface ItemService {
    Page<Item> searchItems(String keyword, Pageable pageable);

    // 등록
    Item saveItem(ItemSaveForm form, LoginSessionInfo seller);

    // 단일 엔티티 반환
    Item getItem(Long id);

    // 상세 DTO
    ItemViewForm getItemViewForm(Long id);

    // 수정 폼 DTO
    ItemEditForm getItemEditForm(Long id);

    // 수정
    void updateItem(Long id, ItemUpdateForm form);

    // 전체 조회
    List<Item> getAllItems();

    // 단일 삭제
    void deleteItem(Long id);

    // 다중 삭제
    void deleteItems(List<Long> itemIds);

    // 지역 코드 조회
    Set<String> getItemRegions(Long itemId);
}
