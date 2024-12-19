package page.admin.item.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import page.admin.item.domain.dto.ItemEditForm;
import page.admin.item.domain.dto.ItemViewForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.service.DeliveryCodeService;
import page.admin.item.service.ItemTypeService;
import page.admin.item.service.RegionService;
import page.admin.member.domain.Member;
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

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final RegionService regionService;        // 추가
    private final ItemTypeService itemTypeService;    // 추가
    private final DeliveryCodeService deliveryCodeService; // 추가

    @GetMapping
    public String items(Model model) {
        model.addAttribute("items", itemService.getAllItems());
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

        // 연관 데이터 추가
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
        model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());


        model.addAttribute("item", new ItemSaveForm());
        return "product/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "loginMember", required = false) LoginSessionInfo loginSessionInfo,
                          Model model) {

        // 유효성 검사 오류가 있는 경우 다시 등록 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
            model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());
            return "product/addForm";
        }

        // 로그인 여부 확인
        if (loginSessionInfo == null) {
            bindingResult.reject("loginRequired", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            // 로그인 정보 전달하여 아이템 저장 처리
            itemService.saveItem(form, loginSessionInfo);
            redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
            return "redirect:/product/items";
        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            bindingResult.reject("fileSaveError", "파일 저장 중 오류가 발생했습니다.");
            return "product/addForm";
        }
    }


    @GetMapping("/{itemId}/edit")
    public String editItemForm(@PathVariable("itemId") Long itemId, Model model) {
        // ItemUpdateForm으로 데이터 매핑
        ItemEditForm itemEditForm = itemService.getItemEditForm(itemId);

        model.addAttribute("itemUpdateForm", itemEditForm);


        // 연관 데이터 추가
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
        model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());

        return "product/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String updateItem(
            @PathVariable("itemId") Long itemId,
            @Valid @ModelAttribute("itemUpdateForm") ItemUpdateForm form,
            BindingResult bindingResult,
            Model model) {

        log.info("Received mainImage : {}", form.getMainImage());
        log.info("Received thumbnails: {}", form.getThumbnails());

        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
            model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());
            return "product/editForm";
        }

        try {
            itemService.updateItem(itemId, form);
        } catch (Exception e) {
            log.error("상품 수정 중 오류 발생", e);
            bindingResult.reject("updateFailed", "상품 수정 중 오류가 발생했습니다.");
            return "product/editForm";
        }

        return "redirect:/product/items/" + itemId;
    }




}
