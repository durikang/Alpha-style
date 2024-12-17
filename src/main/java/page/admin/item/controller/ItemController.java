package page.admin.item.controller;

import page.admin.item.domain.Item;
import page.admin.item.domain.dto.ItemSaveForm;
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

    @GetMapping
    public String items(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "product/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId, @ModelAttribute("alert") Alert alert, Model model) {
        Item item = itemService.getItem(itemId);
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
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product/addForm";
        }

        try {
            itemService.saveItem(form);
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
        model.addAttribute("item", itemService.getItem(itemId));
        return "product/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId, @ModelAttribute Item item) {
        itemService.updateItem(itemId, item);
        return "redirect:/product/items/" + itemId;
    }
}
