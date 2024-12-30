package page.admin.common.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import page.admin.admin.member.domain.dto.AdminSessionInfo;

import jakarta.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/"); // 세션이 없으면 로그인 페이지로 리다이렉트
            return false;
        }

        // 세션에서 LoginSessionInfo 객체 가져오기
        AdminSessionInfo sessionInfo = (AdminSessionInfo) session.getAttribute("loginMember");

        if (sessionInfo == null || !"admin".equalsIgnoreCase(sessionInfo.getRole())) {
            response.sendRedirect("/"); // 관리자가 아니면 로그인 페이지로 리다이렉트
            return false;
        }

        return true; // admin이면 요청 진행
    }
}
