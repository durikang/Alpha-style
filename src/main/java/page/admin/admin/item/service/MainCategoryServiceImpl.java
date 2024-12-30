package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.repository.MainCategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MainCategoryServiceImpl implements MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MainCategory> findAll() {
        return mainCategoryRepository.findAll();
    }

    @Override
    public void save(MainCategory newMainCategory) {
        mainCategoryRepository.save(newMainCategory);
        log.info("새로운 메인 카테고리 추가: {}", newMainCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MainCategory> findById(Long id) {
        return mainCategoryRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (mainCategoryRepository.existsById(id)) {
            mainCategoryRepository.deleteById(id);
            log.info("메인 카테고리 삭제: ID {}", id);
        } else {
            log.warn("삭제하려는 메인 카테고리가 존재하지 않습니다: ID {}", id);
            throw new IllegalArgumentException("삭제하려는 메인 카테고리가 존재하지 않습니다: ID " + id);
        }
    }
}