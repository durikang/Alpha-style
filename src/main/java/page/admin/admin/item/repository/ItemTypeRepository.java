package page.admin.admin.item.repository;

import page.admin.admin.item.domain.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    Optional<ItemType> findByCode(String code); // 코드로 ItemType 조회
}
