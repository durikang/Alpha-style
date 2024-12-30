package page.admin.admin.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import page.admin.admin.item.domain.*;
import page.admin.admin.item.service.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/product/options")
@RequiredArgsConstructor
public class OptionsController {

    private final RegionService regionService;
    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;
    private final ItemTypeService itemTypeService;
    private final DeliveryCodeService deliveryCodeService;

    /**
     * 옵션 관리 페이지 표시
     */
    @GetMapping
    public String showOptionsPage(@RequestParam(value = "activeTab", required = false, defaultValue = "regions") String activeTab,
                                  Model model) {
        // 등록 지역
        List<Region> regions = regionService.findAll();
        model.addAttribute("regions", regions);
        model.addAttribute("newRegion", new Region());

        // 메인 카테고리
        List<MainCategory> mainCategories = mainCategoryService.findAll();
        model.addAttribute("mainCategories", mainCategories);
        model.addAttribute("newMainCategory", new MainCategory());

        // 서브 카테고리
        List<SubCategory> subCategories = subCategoryService.findAll();
        model.addAttribute("subCategories", subCategories);
        model.addAttribute("newSubCategory", new SubCategory());

        // 상품 종류
        List<ItemType> itemTypes = itemTypeService.findAll();
        model.addAttribute("itemTypes", itemTypes);
        model.addAttribute("newItemType", new ItemType());

        // 배송 방식
        List<DeliveryCode> deliveryMethods = deliveryCodeService.findAll();
        model.addAttribute("deliveryMethods", deliveryMethods);
        model.addAttribute("newDeliveryMethod", new DeliveryCode());

        // 활성 탭 정보 추가
        model.addAttribute("activeTab", activeTab);

        return "admin/product/options"; // Thymeleaf 템플릿 이름 (options.html)
    }

    // --------------------- 등록 지역 (Region) CRUD ---------------------

    /**
     * 등록 지역 추가
     */
    @PostMapping("/regions")
    public String createRegion(@ModelAttribute @Valid Region newRegion,
                               BindingResult result,
                               @RequestParam("activeTab") String activeTab,
                               Model model) {
        if (result.hasErrors()) {
            log.warn("지역 추가 시 유효성 검사 실패: {}", result.getAllErrors());
            showOptionsPage(activeTab, model);
            return "product/options";
        }
        regionService.save(newRegion);
        log.info("새로운 지역 추가: {}", newRegion);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 등록 지역 수정 폼 표시
     */
    @GetMapping("/regions/edit/{id}")
    public String editRegionForm(@PathVariable("id") Long id,
                                 @RequestParam(value = "activeTab", required = false, defaultValue = "regions") String activeTab,
                                 Model model) {
        Region region = regionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid region Id:" + id));
        model.addAttribute("entity", region);
        model.addAttribute("entityType", "region");
        model.addAttribute("activeTab", activeTab); // activeTab 전달
        return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
    }

    /**
     * 등록 지역 수정 처리
     */
    @PostMapping("/regions/{id}")
    public String updateRegion(@PathVariable("id") Long id,
                               @ModelAttribute @Valid Region regionDetails,
                               BindingResult result,
                               @RequestParam("activeTab") String activeTab,
                               Model model) {
        if (result.hasErrors()) {
            log.warn("지역 수정 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("entity", regionDetails);
            model.addAttribute("entityType", "region");
            model.addAttribute("activeTab", activeTab);
            return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
        }
        Region region = regionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid region Id:" + id));
        region.setCode(regionDetails.getCode());
        region.setDisplayName(regionDetails.getDisplayName());
        region.setActive(regionDetails.getActive());
        regionService.save(region);
        log.info("지역 수정: {}", region);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 등록 지역 삭제
     */
    @PostMapping("/regions/delete/{id}")
    public String deleteRegion(@PathVariable("id") Long id,
                               @RequestParam("activeTab") String activeTab) {
        regionService.deleteById(id);
        log.info("지역 삭제: ID {}", id);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    // --------------------- 메인 카테고리 (MainCategory) CRUD ---------------------

    /**
     * 메인 카테고리 추가
     */
    @PostMapping("/main-categories")
    public String createMainCategory(@ModelAttribute @Valid MainCategory newMainCategory,
                                     BindingResult result,
                                     @RequestParam("activeTab") String activeTab,
                                     Model model) {
        if (result.hasErrors()) {
            log.warn("메인 카테고리 추가 시 유효성 검사 실패: {}", result.getAllErrors());
            showOptionsPage(activeTab, model);
            return "product/options";
        }
        mainCategoryService.save(newMainCategory);
        log.info("새로운 메인 카테고리 추가: {}", newMainCategory);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 메인 카테고리 수정 폼 표시
     */
    @GetMapping("/main-categories/edit/{id}")
    public String editMainCategoryForm(@PathVariable("id") Long id,
                                       @RequestParam(value = "activeTab", required = false, defaultValue = "main-category") String activeTab,
                                       Model model) {
        MainCategory mainCategory = mainCategoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid main category Id:" + id));
        model.addAttribute("entity", mainCategory);
        model.addAttribute("entityType", "mainCategory");
        model.addAttribute("activeTab", activeTab); // activeTab 전달
        return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
    }

    /**
     * 메인 카테고리 수정 처리
     */
    @PostMapping("/main-categories/{id}")
    public String updateMainCategory(@PathVariable("id") Long id,
                                     @ModelAttribute @Valid MainCategory mainCategoryDetails,
                                     BindingResult result,
                                     @RequestParam("activeTab") String activeTab,
                                     Model model) {
        if (result.hasErrors()) {
            log.warn("메인 카테고리 수정 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("entity", mainCategoryDetails);
            model.addAttribute("entityType", "mainCategory");
            model.addAttribute("activeTab", activeTab);
            return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
        }
        MainCategory mainCategory = mainCategoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid main category Id:" + id));
        mainCategory.setMainCategoryName(mainCategoryDetails.getMainCategoryName());
        mainCategoryService.save(mainCategory);
        log.info("메인 카테고리 수정: {}", mainCategory);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 메인 카테고리 삭제
     */
    @PostMapping("/main-categories/delete/{id}")
    public String deleteMainCategory(@PathVariable("id") Long id,
                                     @RequestParam("activeTab") String activeTab) {
        mainCategoryService.deleteById(id);
        log.info("메인 카테고리 삭제: ID {}", id);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    // --------------------- 서브 카테고리 (SubCategory) CRUD ---------------------

    /**
     * 서브 카테고리 추가
     */
    @PostMapping("/sub-categories")
    public String createSubCategory(@ModelAttribute @Valid SubCategory newSubCategory,
                                    BindingResult result,
                                    @RequestParam("activeTab") String activeTab,
                                    Model model) {
        if (result.hasErrors()) {
            log.warn("서브 카테고리 추가 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("mainCategories", mainCategoryService.findAll());
            showOptionsPage(activeTab, model);
            return "admin/product/options";
        }
        subCategoryService.save(newSubCategory);
        log.info("새로운 서브 카테고리 추가: {}", newSubCategory);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 서브 카테고리 수정 폼 표시
     */
    @GetMapping("/sub-categories/edit/{id}")
    public String editSubCategoryForm(@PathVariable("id") Long id,
                                      @RequestParam(value = "activeTab", required = false, defaultValue = "sub-category") String activeTab,
                                      Model model) {
        SubCategory subCategory = subCategoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sub category Id:" + id));
        model.addAttribute("entity", subCategory);
        model.addAttribute("entityType", "subCategory");
        model.addAttribute("mainCategories", mainCategoryService.findAll());
        model.addAttribute("activeTab", activeTab); // activeTab 전달
        return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
    }

    /**
     * 서브 카테고리 수정 처리
     */
    @PostMapping("/sub-categories/{id}")
    public String updateSubCategory(@PathVariable("id") Long id,
                                    @ModelAttribute @Valid SubCategory subCategoryDetails,
                                    BindingResult result,
                                    @RequestParam("activeTab") String activeTab,
                                    Model model) {
        if (result.hasErrors()) {
            log.warn("서브 카테고리 수정 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("entity", subCategoryDetails);
            model.addAttribute("entityType", "subCategory");
            model.addAttribute("mainCategories", mainCategoryService.findAll());
            model.addAttribute("activeTab", activeTab);
            return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
        }
        SubCategory subCategory = subCategoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sub category Id:" + id));
        subCategory.setSubCategoryName(subCategoryDetails.getSubCategoryName());
        subCategory.setMainCategory(subCategoryDetails.getMainCategory());
        subCategoryService.save(subCategory);
        log.info("서브 카테고리 수정: {}", subCategory);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 서브 카테고리 삭제
     */
    @PostMapping("/sub-categories/delete/{id}")
    public String deleteSubCategory(@PathVariable("id") Long id,
                                    @RequestParam("activeTab") String activeTab) {
        subCategoryService.deleteById(id);
        log.info("서브 카테고리 삭제: ID {}", id);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    // --------------------- 상품 종류 (ItemType) CRUD ---------------------

    /**
     * 상품 종류 추가
     */
    @PostMapping("/item-types")
    public String createItemType(@ModelAttribute @Valid ItemType newItemType,
                                 BindingResult result,
                                 @RequestParam("activeTab") String activeTab,
                                 Model model) {
        if (result.hasErrors()) {
            log.warn("상품 종류 추가 시 유효성 검사 실패: {}", result.getAllErrors());
            showOptionsPage(activeTab, model);
            return "admin/product/options";
        }
        itemTypeService.save(newItemType);
        log.info("새로운 상품 종류 추가: {}", newItemType);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 상품 종류 수정 폼 표시
     */
    @GetMapping("/item-types/edit/{id}")
    public String editItemTypeForm(@PathVariable("id") Long id,
                                   @RequestParam(value = "activeTab", required = false, defaultValue = "item-type") String activeTab,
                                   Model model) {
        ItemType itemType = itemTypeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item type Id:" + id));
        model.addAttribute("entity", itemType);
        model.addAttribute("entityType", "itemType");
        model.addAttribute("activeTab", activeTab); // activeTab 전달
        return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
    }

    /**
     * 상품 종류 수정 처리
     */
    @PostMapping("/item-types/{id}")
    public String updateItemType(@PathVariable("id") Long id,
                                 @ModelAttribute @Valid ItemType itemTypeDetails,
                                 BindingResult result,
                                 @RequestParam("activeTab") String activeTab,
                                 Model model) {
        if (result.hasErrors()) {
            log.warn("상품 종류 수정 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("entity", itemTypeDetails);
            model.addAttribute("entityType", "itemType");
            model.addAttribute("activeTab", activeTab);
            return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
        }
        ItemType itemType = itemTypeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item type Id:" + id));
        itemType.setCode(itemTypeDetails.getCode());
        itemType.setDescription(itemTypeDetails.getDescription());
        itemTypeService.save(itemType);
        log.info("상품 종류 수정: {}", itemType);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 상품 종류 삭제
     */
    @PostMapping("/item-types/delete/{id}")
    public String deleteItemType(@PathVariable("id") Long id,
                                 @RequestParam("activeTab") String activeTab) {
        itemTypeService.deleteById(id);
        log.info("상품 종류 삭제: ID {}", id);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    // --------------------- 배송 방식 (DeliveryCode) CRUD ---------------------

    /**
     * 배송 방식 추가
     */
    @PostMapping("/delivery-methods")
    public String createDeliveryMethod(@ModelAttribute @Valid DeliveryCode newDeliveryMethod,
                                       BindingResult result,
                                       @RequestParam("activeTab") String activeTab,
                                       Model model) {
        if (result.hasErrors()) {
            log.warn("배송 방식 추가 시 유효성 검사 실패: {}", result.getAllErrors());
            showOptionsPage(activeTab, model);
            return "admin/product/options";
        }
        deliveryCodeService.save(newDeliveryMethod);
        log.info("새로운 배송 방식 추가: {}", newDeliveryMethod);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 배송 방식 수정 폼 표시
     */
    @GetMapping("/delivery-methods/edit/{id}")
    public String editDeliveryMethodForm(@PathVariable("id") Long id,
                                         @RequestParam(value = "activeTab", required = false, defaultValue = "delivery-method") String activeTab,
                                         Model model) {
        DeliveryCode deliveryMethod = deliveryCodeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid delivery method Id:" + id));
        model.addAttribute("entity", deliveryMethod);
        model.addAttribute("entityType", "deliveryCode");
        model.addAttribute("activeTab", activeTab); // activeTab 전달
        return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
    }

    /**
     * 배송 방식 수정 처리
     */
    @PostMapping("/delivery-methods/{id}")
    public String updateDeliveryMethod(@PathVariable("id") Long id,
                                       @ModelAttribute @Valid DeliveryCode deliveryMethodDetails,
                                       BindingResult result,
                                       @RequestParam("activeTab") String activeTab,
                                       Model model) {
        if (result.hasErrors()) {
            log.warn("배송 방식 수정 시 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("entity", deliveryMethodDetails);
            model.addAttribute("entityType", "deliveryCode");
            model.addAttribute("activeTab", activeTab);
            return "admin/product/edit-option"; // 공통 수정 페이지 템플릿 (edit-option.html)
        }
        DeliveryCode deliveryMethod = deliveryCodeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid delivery method Id:" + id));
        deliveryMethod.setCode(deliveryMethodDetails.getCode());
        deliveryMethod.setDisplayName(deliveryMethodDetails.getDisplayName());
        deliveryCodeService.save(deliveryMethod);
        log.info("배송 방식 수정: {}", deliveryMethod);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }

    /**
     * 배송 방식 삭제
     */
    @PostMapping("/delivery-methods/delete/{id}")
    public String deleteDeliveryMethod(@PathVariable("id") Long id,
                                       @RequestParam("activeTab") String activeTab) {
        deliveryCodeService.deleteById(id);
        log.info("배송 방식 삭제: ID {}", id);
        return "redirect:/admin/product/options?activeTab=" + activeTab;
    }
}
