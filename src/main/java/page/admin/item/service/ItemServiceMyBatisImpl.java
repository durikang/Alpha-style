package page.admin.item.service;

import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.member.domain.Member;
import page.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceMyBatisImpl implements ItemService {

    private final ItemMapper itemMapper;

    @Override
    public Item saveItem(ItemSaveForm form, Member member) {
        // DTO -> Entity 변환
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());
        item.setOpen(form.getOpen());
        // Region, ItemType, DeliveryCode는 필요시 처리 (DB 조회 필요)

        // MyBatis 저장
        itemMapper.save(item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        return itemMapper.findById(id);
    }

    @Override
    public List<Item> getAllItems() {
        return itemMapper.findAll();
    }

    @Override
    public void updateItem(Long id, Item updateParam) {
        updateParam.setId(id); // ID 설정
        itemMapper.update(updateParam);
    }

    @Override
    public void deleteItem(Long id) {
        itemMapper.delete(id);
    }
}
