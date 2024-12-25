package page.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.item.domain.MainCategory;
import page.admin.item.domain.SubCategory;
import page.admin.item.repository.MainCategoryRepository;
import page.admin.item.repository.SubCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;

    @Override
    public List<SubCategory> getSubCategoriesByMainCategory(Long mainCategoryId) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메인 카테고리 ID입니다: " + mainCategoryId));
        return subCategoryRepository.findAllByMainCategory(mainCategory);
    }
}
