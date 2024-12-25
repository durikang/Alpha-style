package page.admin.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.item.domain.Region;

import java.util.Set;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Set<Region> findByCodeIn(Set<String> codes); // 코드 기반으로 조회
}
