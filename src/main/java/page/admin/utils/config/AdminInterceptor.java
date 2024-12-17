package page.admin.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("/"); // 권한 없으면 로그인 페이지로 리다이렉트
            return false;
        }

        return true; // admin이면 요청 진행
    }
}
