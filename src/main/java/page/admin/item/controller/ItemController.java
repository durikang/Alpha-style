package page.admin.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.item.domain.Item;
import page.admin.item.domain.SubCategory;
import page.admin.item.domain.dto.*;
import page.admin.item.service.*;
import page.admin.member.domain.dto.LoginSessionInfo;
import page.admin.utils.Alert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final RegionService regionService;
    private final ItemTypeService itemTypeService;
    private final DeliveryCodeService deliveryCodeService;
    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;

    @GetMapping
    public String items(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "itemId") String sortField,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") String sortDirection,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 연관 엔티티를 로드하도록 서비스 확인 필요
        Page<Item> items = itemService.searchItems(keyword, sortedPageable);

        int currentPage = items.getNumber() + 1;
        int totalPages = items.getTotalPages();
        int startPage = Math.max(1, (currentPage - 1) / 10 * 10 + 1);
        int endPage = Math.min(startPage + 9, totalPages);

        model.addAttribute("items", items.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "product/items";
    }


    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId, @ModelAttribute("alert") Alert alert, Model model) {
        ItemViewForm item = itemService.getItemViewForm(itemId);
        model.addAttribute("item", item);
        return "product/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        populateModelAttributes(model, null);
        model.addAttribute("item", new ItemSaveForm());
        model.addAttribute("formAction", "/product/items/add");

        log.debug("Form attributes: {}", model.asMap());
        return "product/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo,
                          Model model) {
        if (bindingResult.hasErrors()) {
            populateModelAttributes(model, null);
            return "product/addForm";
        }

        try {
            // 파일 저장 로직 호출
            itemService.saveItem(form, loginSessionInfo);

            redirectAttributes.addFlashAttribute("alert", new Alert("상품이 등록되었습니다.", Alert.AlertType.SUCCESS));
            return "redirect:/product/items";
        } catch (Exception e) {
            log.error("Error during item save", e);
            bindingResult.reject("saveFailed", "상품 저장 중 오류가 발생했습니다.");
            populateModelAttributes(model, null);
            return "product/addForm";
        }
    }


    @GetMapping("/{itemId}/edit")
    public String editItemForm(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemEditForm itemEditForm = itemService.getItemEditForm(itemId);

            if (itemEditForm == null) {
                throw new IllegalArgumentException("해당 ID에 해당하는 상품이 없습니다.");
            }

            populateModelAttributes(model, itemEditForm.getMainCategory());
            model.addAttribute("item", itemEditForm);
            model.addAttribute("formAction", "/product/items/" + itemId + "/edit");
            return "product/editForm";
        } catch (IllegalArgumentException e) {
            log.warn("Invalid itemId {}: {}", itemId, e.getMessage());
            model.addAttribute("alert", new Alert(e.getMessage(), Alert.AlertType.WARNING));
            return "redirect:/product/items";
        } catch (Exception e) {
            log.error("Error fetching itemEditForm for itemId {}: {}", itemId, e.getMessage(), e);
            return "error/errorPage";
        }
    }




    @PostMapping("/{itemId}/edit")
    public String updateItem(
            @PathVariable("itemId") Long itemId,
            @Valid @ModelAttribute("itemEditForm") ItemUpdateForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult, model);
        }

        try {
            itemService.updateItem(itemId, form);
        } catch (Exception e) {
            log.error("Unexpected error during update", e);
            bindingResult.reject("updateFailed", "상품 수정 중 오류가 발생했습니다.");
            return handleValidationErrors(bindingResult, model);
        }

        return "redirect:/product/items/" + itemId;
    }

    @PostMapping("/batch-delete")
    @ResponseBody
    public ResponseEntity<?> deleteItems(@RequestBody List<Long> itemIds) {
        try {
            itemService.deleteItems(itemIds);
            return ResponseEntity.ok("선택한 상품이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "상품 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    // 컨트롤러
    @GetMapping("/subcategories/{mainCategoryId}")
    @ResponseBody
    public List<SubCategoryDTO> getSubCategories(@PathVariable("mainCategoryId") Long mainCategoryId) {
        log.info("Fetching subcategories for mainCategoryId={}", mainCategoryId);

        List<SubCategory> subCategories = subCategoryService.getSubCategoriesByMainCategory(mainCategoryId);
        log.info("Found subcategories: {}", subCategories);

        // DTO 변환
        return subCategories.stream()
                .map(subCategory -> new SubCategoryDTO(subCategory.getId(), subCategory.getSubCategoryName()))
                .collect(Collectors.toList());
    }





    private String handleValidationErrors(BindingResult bindingResult, Model model) {
        populateModelAttributes(model,null);
        return "product/editForm";
    }

    private void populateModelAttributes(Model model, Long mainCategoryId) {
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
        model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());
        model.addAttribute("mainCategories", mainCategoryService.getAllMainCategories());

        log.debug("Main Category ID: {}", mainCategoryId);
        if (mainCategoryId != null) {
            List<SubCategory> subCategories = subCategoryService.getSubCategoriesByMainCategory(mainCategoryId);
            log.debug("Subcategories for Main Category ID {}: {}", mainCategoryId, subCategories);
            model.addAttribute("subCategories", subCategories);
        } else {
            log.debug("Main Category ID is null, skipping subCategories.");
        }
    }

}
