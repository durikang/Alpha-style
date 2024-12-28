package page.admin.admin.item.service;

import page.admin.admin.item.domain.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubCategoryService {
    List<SubCategory> getSubCategoriesByMainCategory(Long mainCategoryId);

    void save(SubCategory newSubCategory);

    Optional<SubCategory> findById(Long id);

    void deleteById(Long id);

    List<SubCategory> findAll();
}