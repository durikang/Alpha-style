package page.admin.common.utils.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.common.utils.Alert;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        // Alert 메시지 생성
        Alert alert = new Alert("업로드할 파일의 크기가 너무 큽니다.", Alert.AlertType.ERROR);
        redirectAttributes.addFlashAttribute("alert", alert);

        // 사용자 정의 에러 페이지로 리다이렉트 (예: 슬라이드 업로드 페이지)
        return "redirect:/admin/slides/upload";
    }
}