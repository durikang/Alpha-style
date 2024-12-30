package page.admin.user.login.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
public class UserHomeController {

    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("userLoginMember");

        if (sessionInfo == null) {
            return "redirect:/login"; // 사용자 로그인 페이지로 리다이렉트
        }

        model.addAttribute("username", sessionInfo.getUsername());
        return "user/userHome";
    }

    // 기타 사용자 관련 핸들러들
}