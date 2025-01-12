package page.admin.user.categories.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.service.MainCategoryService;
import page.admin.admin.manager.service.LogoService;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MainCategoryService mainCategoryService;
    private final LogoService logoService;

    @ModelAttribute("navMainCategories") // 모든 페이지에서 메인 카테고리를 사용 가능
    public List<MainCategory> populateMainCategories() {
        return mainCategoryService.findAll();
    }

    @ModelAttribute("logoPath") // 로고 경로를 모든 페이지에서 사용 가능
    public String populateLogoPath() {
        return logoService.getActiveLogoPath(); // 로고 서비스에서 활성화된 로고 경로 가져오기
    }
}
