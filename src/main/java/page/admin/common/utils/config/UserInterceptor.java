package page.admin.common.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import page.admin.user.member.domain.dto.LoginSessionInfo;

public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return true; // 비회원 접근 허용
        }

        LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginUser");

        if (sessionInfo != null && "member".equalsIgnoreCase(sessionInfo.getUserId())) {
            return true; // 로그인된 사용자 접근 허용
        }

        return true; // 비회원 접근 허용
    }
}
