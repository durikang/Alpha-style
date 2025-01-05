package page.admin.common.utils.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 보호할 URL 패턴을 확인 (예: /user/order-check, /user/mypage 등)
        String uri = request.getRequestURI();

        // 보호할 경로 목록 (추가 가능)
        String[] protectedPaths = {
                "/user/order-check",
                "/user/mypage",
                "/user/cart",
                "/user/place-order"
        };

        // 현재 요청이 보호할 경로 중 하나인지 확인
        boolean isProtected = false;
        for (String path : protectedPaths) {
            if (uri.startsWith(path)) {
                isProtected = true;
                break;
            }
        }

        if (isProtected) {
            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect("/login");
                return false; // 요청을 중단하고 리다이렉트
            }

            LoginSessionInfo sessionInfo = (LoginSessionInfo) session.getAttribute("loginMember");

            if (sessionInfo == null) {
                response.sendRedirect("/login");
                return false; // 요청을 중단하고 리다이렉트
            }

            // 추가적인 역할(Role) 기반 접근 제어가 필요하다면 여기서 체크 가능
            // 예시:
            // if (!"member".equalsIgnoreCase(sessionInfo.getRole())) {
            //     response.sendRedirect("/access-denied");
            //     return false;
            // }
        }

        // 보호할 URL이 아닌 경우, 또는 접근 허용된 경우
        return true;
    }
}
