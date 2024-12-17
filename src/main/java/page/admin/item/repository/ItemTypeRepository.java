package page.admin.item.repository;

import page.admin.item.domain.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
}
