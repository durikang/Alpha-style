package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.domain.SubCategory;
import page.admin.admin.item.repository.MainCategoryRepository;
import page.admin.admin.item.repository.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubCategory> getSubCategoriesByMainCategory(Long mainCategoryId) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메인 카테고리 ID입니다: " + mainCategoryId));
        return subCategoryRepository.findAllByMainCategory(mainCategory);
    }

    @Override
    @Transactional
    public void save(SubCategory newSubCategory) {
        subCategoryRepository.save(newSubCategory);
        log.info("새로운 서브 카테고리 추가: {}", newSubCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubCategory> findById(Long id) {
        return subCategoryRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (subCategoryRepository.existsById(id)) {
            subCategoryRepository.deleteById(id);
            log.info("서브 카테고리 삭제: ID {}", id);
        } else {
            log.warn("삭제하려는 서브 카테고리가 존재하지 않습니다: ID {}", id);
            throw new IllegalArgumentException("삭제하려는 서브 카테고리가 존재하지 않습니다: ID " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategory> findAll() {
        return subCategoryRepository.findAll();
    }
}