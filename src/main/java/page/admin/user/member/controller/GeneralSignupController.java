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
public class GeneralSignupController {

    private final MemberService memberService;

    // 일반 회원 가입 폼
    @GetMapping("/signup/general")
    public String generalSignupForm() {
        return "user/member/signup/general-signup";
    }

    // 일반 회원 가입 처리
    @PostMapping("/signup/general")
    public String generalSignup(Member member, Model model) {
        member.setRole("NORMAL"); // 일반 회원 구분

        try {
            memberService.saveMember(member);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 가입 중 오류가 발생했습니다.");
            return "user/member/signup/general-signup";
        }
    }
}
