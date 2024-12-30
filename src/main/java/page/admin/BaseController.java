package page.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import page.admin.admin.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final MemberService memberService;

    // 일반 사용자 홈 페이지
    @GetMapping("/")
    public String userHome() {
        return "user/userHome"; // 일반 사용자 홈 페이지로 이동
    }


}
