package page.admin.admin.item.service;

import page.admin.admin.item.domain.MainCategory;

import java.util.List;
import java.util.Optional;

public interface MainCategoryService {
    List<MainCategory> findAll(); // 모든 메인 카테고리 조회

    void save(MainCategory newMainCategory);

    Optional<MainCategory> findById(Long id);

    void deleteById(Long id);
}