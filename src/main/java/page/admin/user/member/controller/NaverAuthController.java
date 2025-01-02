package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class NaverAuthController {

    private final MemberService memberService;

    // 네이버 회원 가입 폼
    @GetMapping("/signup/naver")
    public String naverSignupForm() {
        return "user/member/signup/naver-signup";
    }

    // 네이버 회원 가입 처리
    @PostMapping("/signup/naver")
    public String naverSignup(Member naverMember, Model model) {
        // 네이버 회원 정보 가공 (예: ID, 이메일, 이름 제공)
        naverMember.setRole("NAVER");

        try {
            memberService.saveMember(naverMember);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 가입 중 오류가 발생했습니다.");
            return "user/member/naver-signup";
        }
    }
}
