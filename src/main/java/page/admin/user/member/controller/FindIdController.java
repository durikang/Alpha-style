package page.admin.user.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/member/find-id")
public class FindIdController {

    @GetMapping
    public String findId() {
        return "user/member/find/find-id";
    }
}
