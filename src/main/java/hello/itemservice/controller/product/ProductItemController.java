package hello.itemservice.controller.product;

import hello.itemservice.domain.item.*;
import hello.itemservice.domain.item.form.ItemSaveForm;
import hello.itemservice.domain.utils.Alert;
import hello.itemservice.domain.utils.CommonDataProvider;
import hello.itemservice.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class ProductItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ItemType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(new ItemType(text, null)); // 코드만 설정 (description은 null)
            }

            @Override
            public String getAsText() {
                ItemType itemType = (ItemType) getValue();
                return (itemType != null) ? itemType.getCode() : ""; // null일 경우 빈 문자열 반환
            }
        });
    }

    @ModelAttribute("regions")
    public List<Region> regions() {
        return CommonDataProvider.getRegions();
    }

    @ModelAttribute("itemTypes")
    public List<ItemType> itemTypes() {
        return CommonDataProvider.getItemTypes();
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        return CommonDataProvider.getDeliveryStatuses();
    }

    @GetMapping
    public String items(Model model) {

        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "product/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId,@ModelAttribute("alert") Alert alert, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        // 숫자 포맷팅
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        String formattedPrice = numberFormat.format(item.getPrice());
        model.addAttribute("formattedPrice", formattedPrice);

        // 배송 방식의 displayName 찾기
        String deliveryDisplayName = deliveryCodes().stream()
                .filter(dc -> dc.getCode().equals(item.getDeliveryCode()))
                .map(DeliveryCode::getDisplayName)
                .findFirst()
                .orElse("알 수 없음");
        model.addAttribute("deliveryDisplayName", deliveryDisplayName);

        // Alert를 모델에 추가 (자동으로 추가되지만 명시적으로 넣을 수 있음)
        if (alert != null) {
            model.addAttribute("alert", alert);
        }

        return "product/item";
    }


    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "product/addForm";
    }

    @PostMapping("/add")
    public String addItem(
            @Validated @ModelAttribute("item") ItemSaveForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("폼 데이터: {}", form);

        // 파일 검증
        if (form.getMainImage() == null || form.getMainImage().isEmpty()) {
            bindingResult.rejectValue("mainImage", "required", "메인 이미지는 필수입니다.");
        }
        if (form.getThumbnails() == null || form.getThumbnails().isEmpty()) {
            bindingResult.rejectValue("thumbnails", "required", "썸네일 이미지는 필수입니다.");
        } else if (form.getThumbnails().size() > 4) {
            bindingResult.rejectValue("thumbnails", "maxExceeded", "썸네일 이미지는 최대 4개까지 가능합니다.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("유효성 검증 실패: {}", bindingResult.getAllErrors());
            return "product/addForm";
        }

        try {
            // 파일 저장
            UploadFile mainImage = fileStore.storeFile(form.getMainImage());
            List<UploadFile> thumbnails = fileStore.storeFiles(form.getThumbnails());

            // Item 생성 및 저장
            Item item = new Item(
                    form.getItemName(),
                    form.getPrice(),
                    form.getQuantity(),
                    form.getOpen(),
                    form.getRegions(),
                    form.getItemType(),
                    form.getDeliveryCode(),
                    mainImage,      // 저장된 메인 이미지 정보
                    thumbnails      // 저장된 썸네일 정보
            );
            itemRepository.save(item);

            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 등록되었습니다.");
            return "redirect:/product/items";
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            bindingResult.reject("fileUploadError", "파일 업로드 중 오류가 발생했습니다.");
            return "product/addForm";
        }
    }




    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item); // Item 객체만 추가
        return "product/editForm";
    }


    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId,
                       @ModelAttribute Item item,
                       @RequestParam("regions") List<String> regionCodes,
                       @RequestParam("itemType") String itemTypeCode, // 추가: itemType 코드
                       @ModelAttribute("regions") List<Region> availableRegions,
                       @ModelAttribute("itemTypes") List<ItemType> availableItemTypes) { // 추가: itemTypes 데이터
        log.info("수정 요청 - itemId: {}", itemId);
        log.info("수정된 regions 코드: {}", regionCodes);
        log.info("수정된 itemType 코드: {}", itemTypeCode);

        // 등록 지역 매핑
        List<Region> matchedRegions = availableRegions.stream()
                .filter(region -> regionCodes.contains(region.getCode()))
                .collect(Collectors.toList());
        item.setRegions(matchedRegions);

        // 상품 종류 매핑
        ItemType matchedItemType = availableItemTypes.stream()
                .filter(type -> type.getCode().equals(itemTypeCode))
                .findFirst()
                .orElse(null); // 매칭되지 않으면 null로 설정
        item.setItemType(matchedItemType);

        // 로그 출력
        log.info("변환된 regions 객체: {}", matchedRegions);
        log.info("변환된 itemType 객체: {}", matchedItemType);

        // Item 업데이트
        itemRepository.update(itemId, item);

        return "redirect:/product/items/{itemId}";
    }





}