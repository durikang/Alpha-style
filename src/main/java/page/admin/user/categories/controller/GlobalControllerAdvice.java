package page.admin.user.categories.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.service.MainCategoryService;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MainCategoryService mainCategoryService;

    @ModelAttribute("navMainCategories") // 변경된 이름
    public List<MainCategory> populateMainCategories() {
        return mainCategoryService.findAll();
    }
}
