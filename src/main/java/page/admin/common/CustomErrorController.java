package page.admin.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import page.admin.member.service.MemberServiceImpl;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        logger.error("에러 발생! 상태 코드: {}, 요청 URI: {}", statusCode, requestUri);

        // 에러 메시지 기본 설정
        String errorMessage = "알 수 없는 에러가 발생했습니다.";
        if (statusCode != null) {
            switch (statusCode) {
                case 404:
                    errorMessage = "페이지를 찾을 수 없습니다.";
                    break;
                case 500:
                    errorMessage = "서버 내부 오류가 발생했습니다.";
                    break;
                default:
                    errorMessage = "예기치 못한 오류가 발생했습니다.";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        return "error/errorPage";
    }
}
