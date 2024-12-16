package hello.itemservice.controller;

import hello.itemservice.domain.users.LoginForm;
import hello.itemservice.domain.users.Member;
import hello.itemservice.domain.users.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final MemberRepository memberRepository;

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

        // 유효성 검증 실패 시 처리
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginForm);
            return "index"; // 에러 메시지는 Thymeleaf에서 `th:errors`를 통해 처리
        }

        // 사용자 인증
        Member member = memberRepository.findAll()
                .stream()
                .filter(m -> m.getUserId().equals(loginForm.getUserId())
                        && m.getPassword().equals(loginForm.getPassword())) // 암호화 미사용
                .findFirst()
                .orElse(null);

        // 인증 성공
        if (member != null && "admin".equals(member.getRole())) {
            session.setAttribute("username", member.getUsername());
            session.setAttribute("role", member.getRole());
            return "redirect:/"; // 홈 화면으로 이동
        }

        // 인증 실패 메시지 추가
        model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
        model.addAttribute("loginMember", loginForm);
        return "index"; // 로그인 화면으로 복귀
    }





    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/"; // 로그아웃 후 로그인 페이지로
    }
}
