package page.admin.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import page.admin.admin.manager.domain.Slider;
import page.admin.admin.manager.service.SliderService;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserHomeController {

    private final SliderService sliderService;

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

        return "user/userHome";
    }
}

