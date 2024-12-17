package page.admin.item.repository;

import page.admin.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> { // ID 타입은 Long입니다.
}
