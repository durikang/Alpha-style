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
import page.admin.member.domain.dto.LoginSessionInfo;
import page.admin.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final MemberService memberService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // 세션에서 LoginSessionInfo 객체를 가져옴
        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginMember");

        // 로그인된 사용자 정보 확인
        if (sessionInfo != null && "admin".equalsIgnoreCase(sessionInfo.getRole())) {
            model.addAttribute("username", sessionInfo.getUsername());
            return "home"; // 관리자를 위한 홈 화면
        }

        // 로그인되지 않은 경우
        model.addAttribute("loginMember", new LoginForm());
        return "index"; // 로그인 페이지
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

        if (member.isPresent() && "admin".equalsIgnoreCase(member.get().getRole())) {
            // 세션에 DTO 객체 저장
            LoginSessionInfo sessionInfo = new LoginSessionInfo(
                    member.get().getUserNo(),  // 사용자 번호 (PK)
                    member.get().getUserId(),  // 로그인 ID
                    member.get().getUsername(),// 사용자 이름
                    member.get().getRole(),    // 역할
                    member.get().getEmail()    // 이메일
            );
            session.setAttribute("loginMember", sessionInfo);
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
