package page.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.item.domain.Region;
import page.admin.item.repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }
}
