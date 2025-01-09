package page.admin.common.utils.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.manager.service.FileSettingsService;
import page.admin.common.utils.Alert;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final FileSettingsService fileSettingsService;

    public GlobalExceptionHandler(FileSettingsService fileSettingsService) {
        this.fileSettingsService = fileSettingsService;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        long maxFileSizeMB = fileSettingsService.getSettings().getMaxFileSize() / (1024 * 1024);
        redirectAttributes.addFlashAttribute("alert",
                new Alert("파일 크기가 너무 큽니다. 업로드 가능한 최대 크기는 " + maxFileSizeMB + "MB 입니다.", Alert.AlertType.ERROR));
        return "redirect:/admin/user/slider/add"; // 파일 업로드 폼으로 리다이렉트
    }
}