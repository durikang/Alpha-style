package page.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import page.admin.member.domain.dto.LoginForm;
import page.admin.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final MemberService memberService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("role");

        if (loggedInUser != null && "admin".equals(userRole)) {
            model.addAttribute("username", loggedInUser);
            return "home";
        }
        model.addAttribute("loginMember", new LoginForm());
        return "index";
    }

    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute LoginForm loginForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginForm);
            return "index";
        }

        // 사용자 인증
        var member = memberService.findByUserIdAndPassword(loginForm.getUserId(), loginForm.getPassword());

        if (member.isPresent() && "admin".equals(member.get().getRole())) {
            session.setAttribute("username", member.get().getUsername());
            session.setAttribute("role", member.get().getRole());
            return "redirect:/";
        }

        // 인증 실패 시
        model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
        model.addAttribute("loginMember", loginForm);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
