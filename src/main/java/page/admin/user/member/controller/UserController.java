package page.admin.user.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // 회원 가입 선택 페이지로 이동
    @GetMapping("/signup")
    public String showSignupOptions() {
        return "user/member/signup/signup-options"; // signup-options.html 템플릿으로 이동
    }
}
