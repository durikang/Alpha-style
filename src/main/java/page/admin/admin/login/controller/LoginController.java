package page.admin.admin.login.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import page.admin.admin.member.domain.dto.LoginForm;
import page.admin.admin.member.service.MemberService;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginMember");

        if (sessionInfo != null) {
            return redirectToHome(sessionInfo.getRole());
        }

        model.addAttribute("loginMember", new LoginForm());
        return "login"; // 로그인 페이지 템플릿
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute("loginMember") LoginForm loginForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginForm);
            return "login"; // 로그인 페이지 템플릿
        }

        var member = memberService.findByUserIdAndPassword(loginForm.getUserId(), loginForm.getPassword());
        if (member.isPresent()) {
            LoginSessionInfo sessionInfo = new LoginSessionInfo(
                    member.get().getUserNo(),
                    member.get().getUserId(),
                    member.get().getUsername(),
                    member.get().getEmail(),
                    member.get().getAddress(),
                    member.get().getMobilePhone(),
                    member.get().getGender(),
                    member.get().getRole()
            );
            session.setAttribute("loginMember", sessionInfo);
            return redirectToHome(member.get().getRole());
        }

        model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
        model.addAttribute("loginMember", loginForm);
        return "login";
    }



    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // 역할에 따른 리다이렉트
    private String redirectToHome(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            return "redirect:/admin/adminHome";
        } else if ("member".equalsIgnoreCase(role)) {
            return "redirect:/user/home";
        }
        return "redirect:/login";
    }
}
