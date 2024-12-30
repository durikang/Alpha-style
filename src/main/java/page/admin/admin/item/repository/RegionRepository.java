package page.admin.admin.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.item.domain.Region;

import java.util.Set;

public interface RegionRepository extends JpaRepository<Region, Long> {
    // code가 Set<String> 범위 내에 있는 Region들 조회
    Set<Region> findByCodeIn(Set<String> codes);
}
