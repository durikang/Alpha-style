package page.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import page.admin.admin.member.domain.dto.AdminSessionInfo;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
public class BaseController {

    /**
     * 일반 사용자 홈 페이지
     * 로그인 여부와 관계없이 접근 가능
     */
    @GetMapping({"/", "/user", "/user/home"})
    public String userHome(HttpSession session) {
        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginUser");

        // 로그인된 일반 사용자일 경우에도 userHome으로 이동
        if (sessionInfo != null) {
            return "user/userHome";
        }

        // 비로그인 상태에서도 userHome으로 이동
        return "user/userHome";
    }

    /**
     * 관리자 홈 페이지
     * ADMIN 권한이 있는 사용자만 접근 가능
     */
    @GetMapping("/admin/adminHome")
    public String adminHome(HttpSession session) {
        if (!hasAdminAccess(session)) {
            return "redirect:/admin"; // 권한 없으면 로그인 페이지로 리다이렉트
        }
        return "admin/adminHome"; // 관리자 홈 페이지
    }

    /**
     * 관리자 권한 체크 메서드
     */
    private boolean hasAdminAccess(HttpSession session) {
        Object sessionInfo = session.getAttribute("loginMember");
        return sessionInfo instanceof AdminSessionInfo
                && "admin".equalsIgnoreCase(((AdminSessionInfo) sessionInfo).getRole());
    }
}
