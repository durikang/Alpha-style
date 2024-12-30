package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.Region;
import page.admin.admin.item.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    @Override
    public void save(Region newRegion) {
        regionRepository.save(newRegion);
        log.info("새로운 지역 추가: {}", newRegion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Region> findById(Long id) {
        return regionRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (regionRepository.existsById(id)) {
            regionRepository.deleteById(id);
            log.info("지역 삭제: ID {}", id);
        } else {
            log.warn("삭제하려는 지역이 존재하지 않습니다: ID {}", id);
            throw new IllegalArgumentException("삭제하려는 지역이 존재하지 않습니다: ID " + id);
        }
    }
}