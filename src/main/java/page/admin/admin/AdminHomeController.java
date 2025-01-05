package page.admin.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHomeController {

    @GetMapping("/adminHome")
    public String adminHome(HttpSession session) {
        if (!hasAdminAccess(session)) {
            return "redirect:/login";
        }
        return "admin/adminHome";
    }

    private boolean hasAdminAccess(HttpSession session) {
        Object sessionInfo = session.getAttribute("loginMember");
        if (sessionInfo instanceof LoginSessionInfo) {
            LoginSessionInfo loginInfo = (LoginSessionInfo) sessionInfo;
            return "admin".equalsIgnoreCase(loginInfo.getRole());
        }
        return false;
    }
}

