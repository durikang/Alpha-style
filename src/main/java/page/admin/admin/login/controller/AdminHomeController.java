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
import page.admin.admin.member.domain.dto.AdminSessionInfo;
import page.admin.admin.member.domain.dto.LoginForm;
import page.admin.admin.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class AdminHomeController {

    private final MemberService memberService;
    // 어드민 로그인 페이지
    @GetMapping({"/admin", "/admin/"})
    public String adminLoginPage(HttpSession session, Model model) {
        // 세션에서 로그인된 사용자 확인
        AdminSessionInfo sessionInfo = (AdminSessionInfo) session.getAttribute("loginMember");

        // 이미 로그인된 경우 관리자 홈으로 리다이렉트
        if (sessionInfo != null && "admin".equalsIgnoreCase(sessionInfo.getRole())) {
            return "redirect:admin/adminHome";
        }

        // 로그인되지 않은 경우 로그인 폼 추가 후 로그인 페이지 반환
        model.addAttribute("loginMember", new LoginForm());
        return "/admin/login";
    }

    // 어드민 로그인 처리
    @PostMapping("/admin/login")
    public String adminLogin(
            @Validated @ModelAttribute LoginForm loginForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginForm);
            return "admin/login"; // 오류 시 로그인 페이지로 반환
        }

        var member = memberService.findByUserIdAndPassword(loginForm.getUserId(), loginForm.getPassword());
        if (member.isPresent() && "admin".equalsIgnoreCase(member.get().getRole())) {
            // 세션에 로그인 정보 저장
            AdminSessionInfo sessionInfo = new AdminSessionInfo(
                    member.get().getUserNo(),
                    member.get().getUserId(),
                    member.get().getUsername(),
                    member.get().getRole(),
                    member.get().getEmail()
            );
            session.setAttribute("loginMember", sessionInfo);
            return "redirect:/admin/adminHome";
        }

        // 인증 실패 시
        model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
        model.addAttribute("loginMember", loginForm);
        return "admin/login";
    }

    // 어드민 홈 페이지
    @GetMapping("/admin/adminHome")
    public String adminHome(HttpSession session, Model model) {
        AdminSessionInfo sessionInfo = (AdminSessionInfo) session.getAttribute("loginMember");

        if (sessionInfo == null || !"admin".equalsIgnoreCase(sessionInfo.getRole())) {
            return "redirect:/admin"; // 로그인되지 않았거나 관리자가 아니면 로그인 페이지로 이동
        }

        model.addAttribute("username", sessionInfo.getUsername());
        return "admin/adminHome";
    }

    // 로그아웃 처리
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin"; // 로그아웃 후 로그인 페이지로 이동
    }
}
