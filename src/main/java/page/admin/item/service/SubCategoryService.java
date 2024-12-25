package page.admin.item.service;

import page.admin.item.domain.SubCategory;

import java.util.List;

public interface SubCategoryService {
    List<SubCategory> getSubCategoriesByMainCategory(Long mainCategoryId);
}
