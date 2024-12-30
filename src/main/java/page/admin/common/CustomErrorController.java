package page.admin.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        logger.error("에러 발생! 상태 코드: {}, 요청 URI: {}", statusCode, requestUri);

        // 상태 코드 null 처리
        if (statusCode == null) {
            model.addAttribute("errorMessage", "예기치 못한 에러가 발생했습니다.");
            return "common/error/general";
        }
        // 상태 코드에 따른 뷰 반환
        if (statusCode != null) {
            switch (statusCode) {
                case 404:
                    return "common/error/404";
                case 500:
                    return "common/error/500";
                default:
                    model.addAttribute("statusCode", statusCode);
                    return "common/error/general";
            }
        }

        return "common/error/general";
    }

}
