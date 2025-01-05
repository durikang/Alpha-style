package page.admin.admin.item.controller;

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
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.SubCategory;
import page.admin.admin.item.domain.dto.*;
import page.admin.admin.item.service.*;
import page.admin.common.utils.Alert;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/product/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final RegionService regionService;
    private final ItemTypeService itemTypeService;
    private final DeliveryCodeService deliveryCodeService;
    private final MainCategoryService mainCategoryService;
    private final SubCategoryService subCategoryService;

    /**
     * 상품 목록
     */
    @GetMapping
    public String items(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "sort", required = false, defaultValue = "itemId") String sortField,
                        @RequestParam(value = "direction", required = false, defaultValue = "ASC") String sortDirection,
                        @PageableDefault(size = 10) Pageable pageable,
                        Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

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

        return "admin/product/items"; // 선행 슬래시 제거
    }

    /**
     * 단일 상품 조회
     */
    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId,
                       @ModelAttribute("alert") Alert alert,
                       Model model) {
        ItemViewForm item = itemService.getItemViewForm(itemId);
        model.addAttribute("item", item); // 상세보기용 DTO
        return "admin/product/item"; // 선행 슬래시 제거
    }

    /**
     * 상품 등록 폼
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        // 폼에서 사용될 기본 select/checkbox 옵션들
        populateModelAttributes(model, null);

        // 등록 시에는 ItemSaveForm을 사용
        model.addAttribute("item", new ItemSaveForm());
        // 폼 액션
        model.addAttribute("formAction", "/admin/product/items/add"); // 어드민 URL 포함

        log.debug("Form attributes: {}", model.asMap());
        return "admin/product/addForm"; // 선행 슬래시 제거
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo,
                          Model model) {

        if (bindingResult.hasErrors()) {
            populateModelAttributes(model, null);
            return "admin/product/addForm"; // 선행 슬래시 제거
        }

        try {
            itemService.saveItem(form, loginSessionInfo);
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("상품이 등록되었습니다.", Alert.AlertType.SUCCESS));
            return "redirect:/admin/product/items"; // 이중 슬래시 제거
        } catch (Exception e) {
            log.error("Error during item save", e);
            bindingResult.reject("saveFailed", "상품 저장 중 오류가 발생했습니다.");
            populateModelAttributes(model, null);
            return "admin/product/addForm"; // 선행 슬래시 제거
        }
    }

    /**
     * 상품 수정 폼
     */
    @GetMapping("/{itemId}/edit")
    public String editItemForm(@PathVariable("itemId") Long itemId,
                               Model model) {
        try {
            // 서비스에서 수정 전용 DTO (ItemEditForm or ItemUpdateForm)를 가져온다
            ItemEditForm itemEditForm = itemService.getItemEditForm(itemId);
            if (itemEditForm == null) {
                throw new IllegalArgumentException("해당 ID에 해당하는 상품이 없습니다.");
            }

            // 해당 상품의 메인 카테고리 ID를 가져와 서브 카테고리 세팅
            populateModelAttributes(model, itemEditForm.getMainCategory());

            // thymeleaf의 th:object가 참조할 key="item"
            model.addAttribute("item", itemEditForm);

            // 폼 액션은 "/admin/product/items/{itemId}/edit"
            model.addAttribute("formAction", "/admin/product/items/" + itemId + "/edit"); // 어드민 URL 포함
            return "admin/product/editForm"; // 선행 슬래시 제거
        } catch (IllegalArgumentException e) {
            log.warn("Invalid itemId {}: {}", itemId, e.getMessage());
            model.addAttribute("alert", new Alert(e.getMessage(), Alert.AlertType.WARNING));
            return "redirect:/admin/product/items"; // 이중 슬래시 제거
        } catch (Exception e) {
            log.error("Error fetching itemEditForm for itemId {}: {}", itemId, e.getMessage(), e);
            return "common/error/errorPage";
        }
    }

    /**
     * 상품 수정 처리
     */
    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId,
                             @Valid @ModelAttribute("item") ItemUpdateForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            // validate 실패 시 다시 카테고리/지역 옵션을 세팅
            populateModelAttributes(model, form.getMainCategory());
            return "admin/product/editForm"; // 선행 슬래시 제거
        }

        try {
            itemService.updateItem(itemId, form);
            redirectAttributes.addFlashAttribute("alert",
                    new Alert("상품이 업데이트 되었습니다.", Alert.AlertType.SUCCESS));

        } catch (Exception e) {
            log.error("Unexpected error during update", e);
            bindingResult.reject("updateFailed", "상품 수정 중 오류가 발생했습니다.");
            populateModelAttributes(model, form.getMainCategory());
            return "admin/product/editForm"; // 선행 슬래시 제거
        }
        return "redirect:/admin/product/items/" + itemId; // 이중 슬래시 제거
    }

    /**
     * 다중 상품 삭제
     */
    @PostMapping("/batch-delete")
    @ResponseBody
    public ResponseEntity<?> deleteItems(@RequestBody List<Long> itemIds) {
        try {
            if (itemIds == null || itemIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "삭제할 상품 ID가 제공되지 않았습니다."));
            }

            itemService.deleteItems(itemIds);
            return ResponseEntity.ok(Map.of("message", "선택한 상품이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("Error deleting items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "상품 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 특정 메인 카테고리에 해당하는 서브 카테고리 목록을 반환 (Ajax)
     */
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

    /**
     * 폼에서 필요한 공통 모델 데이터 세팅
     */
    private void populateModelAttributes(Model model, Long mainCategoryId) {
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("itemTypes", itemTypeService.findAll());
        model.addAttribute("deliveryCodes", deliveryCodeService.findAll());
        model.addAttribute("mainCategories", mainCategoryService.findAll());

        // 메인 카테고리가 지정되어 있다면 해당 서브 카테고리 리스트 로딩
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
