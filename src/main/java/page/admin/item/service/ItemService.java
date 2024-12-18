package page.admin.item.service;

import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.member.domain.Member;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    Item saveItem(ItemSaveForm form, Member member) throws IOException; // DTO로 아이템 저장
    Item getItem(Long id); // 아이템 단건 조회
    List<Item> getAllItems(); // 아이템 전체 조회
    void updateItem(Long id, Item updateParam); // 아이템 수정
    void deleteItem(Long id); // 아이템 삭제
}
