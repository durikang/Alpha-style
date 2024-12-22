package page.admin.item.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import page.admin.item.domain.Item;
import page.admin.item.domain.UploadFile;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.service.DeliveryCodeService;
import page.admin.item.service.ItemTypeService;
import page.admin.item.service.RegionService;
import page.admin.member.domain.dto.LoginSessionInfo;
import page.admin.utils.Alert;
import page.admin.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.utils.exception.FileProcessingException;
import page.admin.utils.file.FileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final RegionService regionService;
    private final ItemTypeService itemTypeService;
    private final DeliveryCodeService deliveryCodeService;

    @GetMapping
    public String items(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "itemId") String sortField,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") String sortDirection,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // 정렬 설정
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        // 페이지 요청 생성 (정렬 적용)
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 검색 및 페이징 처리
        Page<Item> items = itemService.searchItems(keyword, sortedPageable);

        // 모델에 데이터 추가
        model.addAttribute("items", items.getContent());
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("currentPage", items.getNumber() + 1);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "product/items"; // HTML 템플릿 경로
    }


    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId, @ModelAttribute("alert") Alert alert, Model model) {
        ItemViewForm item = itemService.getItemViewForm(itemId);
        model.addAttribute("item", item);
        return "product/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        populateModelAttributes(model); // 공통 데이터 설정
        model.addAttribute("item", new ItemSaveForm());
        return "product/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo,
                          Model model) {

        if (bindingResult.hasErrors()) {
            populateModelAttributes(model); // 공통 데이터 설정
            return "product/addForm";
        }

        if (loginSessionInfo == null) {
            bindingResult.reject("loginRequired", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            itemService.saveItem(form, loginSessionInfo);
            redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
            return "redirect:/product/items";
        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            bindingResult.reject("fileSaveError", "파일 저장 중 오류가 발생했습니다.");
            populateModelAttributes(model); // 공통 데이터 설정
            return "product/addForm";
        }
    }

    @GetMapping("/{itemId}/edit")
    public String editItemForm(@PathVariable("itemId") Long itemId, Model model) {
        try {
            // ItemEditForm 데이터 가져오기
            ItemEditForm itemEditForm = itemService.getItemEditForm(itemId);

            // thumbnails 초기화 (null 방지)
            if (itemEditForm.getThumbnails() == null) {
                itemEditForm.setThumbnails(new ArrayList<>()); // 빈 리스트로 설정
            }

            // 공통 데이터 설정
            populateModelAttributes(model);

            // 모델에 itemEditForm 추가
            model.addAttribute("itemEditForm", itemEditForm);

            return "product/editForm"; // 템플릿 반환
        } catch (Exception e) {
            // 로그 기록
            log.error("Error fetching itemEditForm for itemId {}: {}", itemId, e.getMessage());

            // 오류 페이지로 이동 (404 혹은 에러 페이지)
            return "error/errorPage";
        }
    }


    @PostMapping("/{itemId}/edit")
    public String updateItem(
            @PathVariable("itemId") Long itemId,
            @Valid @ModelAttribute("itemEditForm") ItemUpdateForm form,
            BindingResult bindingResult,
            Model model) {

        log.info("Received mainImage : {}", form.getMainImage());
        log.info("Received thumbnails: {}", form.getThumbnails());

        // 유효성 검사 실패 처리
        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            populateModelAttributes(model); // 공통 데이터 설정
            return "product/editForm";
        }

        try {
            // 아이템 업데이트
            itemService.updateItem(itemId, form);

        } catch (FileProcessingException e) {
            log.error("파일 처리 중 오류 발생", e);
            bindingResult.reject("fileProcessingError", "파일 처리 중 오류가 발생했습니다.");
            populateModelAttributes(model); // 공통 데이터 설정
            return "product/editForm";

        } catch (Exception e) {
            log.error("상품 수정 중 오류 발생", e);
            bindingResult.reject("updateFailed", "상품 수정 중 오류가 발생했습니다.");
            populateModelAttributes(model); // 공통 데이터 설정
            return "product/editForm";
        }

        // 수정 성공 시 리다이렉트
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




    // 공통 데이터 설정 메서드
    private void populateModelAttributes(Model model) {
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
        model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());
    }
}
