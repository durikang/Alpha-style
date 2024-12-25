package page.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.item.domain.MainCategory;
import page.admin.item.repository.MainCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainCategoryServiceImpl implements MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Override
    public List<MainCategory> getAllMainCategories() {
        return mainCategoryRepository.findAll();
    }
}
