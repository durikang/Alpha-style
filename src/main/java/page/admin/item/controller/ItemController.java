package page.admin.item.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemDetailForm;
import page.admin.item.domain.dto.ItemSaveForm;
import page.admin.item.domain.dto.ItemUpdateForm;
import page.admin.item.service.DeliveryCodeService;
import page.admin.item.service.ItemTypeService;
import page.admin.item.service.RegionService;
import page.admin.member.domain.Member;
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
        ItemDetailForm item = itemService.getItem(itemId);
        model.addAttribute("item", item);
        return "product/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new ItemSaveForm());
        return "product/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

        if (bindingResult.hasErrors()) {
            return "product/addForm";
        }

        if (loginMember == null) {
            bindingResult.reject("loginRequired", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            itemService.saveItem(form, loginMember); // 로그인 사용자 정보 전달
            redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
            return "redirect:/product/items";
        } catch (Exception e) {
            log.error("파일 저장 실패", e);
            bindingResult.reject("fileSaveError", "파일 저장 중 오류가 발생했습니다.");
            return "product/addForm";
        }
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId, Model model) {
        // ItemUpdateForm으로 데이터 매핑
        ItemUpdateForm itemUpdateForm = itemService.getItemForUpdate(itemId);


        System.out.println("itemUpdateForm : " + itemUpdateForm);


        model.addAttribute("itemUpdateForm", itemUpdateForm);

        // 연관 데이터 추가
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("itemTypes", itemTypeService.getAllItemTypes());
        model.addAttribute("deliveryCodes", deliveryCodeService.getAllDeliveryCodes());

        return "product/editForm";
    }


    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId,
                             @Valid @ModelAttribute("itemUpdateForm") ItemUpdateForm form,
                             BindingResult bindingResult,
                             HttpSession session,Model model) {
        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            // 에러 메시지와 함께 폼으로 리다이렉트
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "product/editForm"; // 수정 폼 뷰로 반환
        }

        // 서비스 호출 (IOException은 서비스에서 처리됨)
        itemService.updateItem(itemId, form);

        return "redirect:/product/items/" + itemId;
    }

}
