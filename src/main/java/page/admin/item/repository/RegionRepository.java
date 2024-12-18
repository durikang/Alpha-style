package page.admin.item.repository;

import page.admin.item.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByCodeIn(List<String> codes); // 코드 기반으로 조회
}
