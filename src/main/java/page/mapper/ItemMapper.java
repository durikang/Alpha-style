package page.mapper;

import page.admin.item.domain.Item;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    void save(Item item); // Item 엔티티를 저장

    Item findById(Long id); // ID로 조회

    List<Item> findAll(); // 모든 데이터 조회

    void update(Item item); // 데이터 수정

    void delete(Long id); // 데이터 삭제
}
