package page.admin.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.service.ItemService;
import page.admin.admin.manager.domain.Slider;
import page.admin.admin.manager.domain.dto.CategoryWithItemsDTO;
import page.admin.admin.manager.service.SliderService;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserHomeController {

    private final SliderService sliderService;
    private final ItemService itemService;

    @GetMapping({"/", "/home"})
    public String userHome(HttpSession session, Model model) {
        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginMember");

        if (sessionInfo != null) {
            model.addAttribute("loginMember", sessionInfo);
        }

        List<Slider> sliders = sliderService.getAllSliders().stream()
                .filter(Slider::getActive)
                .toList();
        model.addAttribute("sliders", sliders);

        // 메인 카테고리별로 상품 조회 (각 카테고리당 최대 4개)
        List<CategoryWithItemsDTO> categoriesWithItems = itemService.getItemsGroupedByMainCategory(4);
        model.addAttribute("categoriesWithItems", categoriesWithItems);

        return "user/userHome";
    }

    @GetMapping("/product/items/more")
    @ResponseBody
    public List<Item> loadMoreItems(@RequestParam Long mainCategoryId, @RequestParam int offset, @RequestParam int limit) {
        return itemService.findItemsByMainCategoryWithOffset(mainCategoryId, offset, limit);
    }


}