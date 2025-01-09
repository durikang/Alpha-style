package page.admin.common.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/login"); // 세션이 없으면 로그인 페이지로 리다이렉트
            return false;
        }

        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginMember");

        if (sessionInfo == null || sessionInfo.getRole() == null ||
                (!"admin".equalsIgnoreCase(sessionInfo.getRole()))) {
            // 일반 사용자는 user 홈으로 리다이렉트
            response.sendRedirect(sessionInfo == null ? "/login" : "/user/home");
            return false;
        }

        return true; // admin이면 요청 진행
    }
}
