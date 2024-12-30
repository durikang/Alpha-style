package page.admin.admin.item.service;

import page.admin.admin.item.domain.Region;

import java.util.List;
import java.util.Optional;

public interface RegionService {
    List<Region> findAll(); // 모든 지역 조회

    void save(Region newRegion);

    Optional<Region> findById(Long id);

    void deleteById(Long id);
}
