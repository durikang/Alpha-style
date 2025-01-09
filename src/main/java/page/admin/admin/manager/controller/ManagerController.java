package page.admin.admin.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.manager.domain.FileSettings;
import page.admin.admin.manager.service.FileSettingsService;
import page.admin.common.utils.Alert;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/manager")
public class ManagerController {

    private final FileSettingsService fileSettingsService;

    // 설정 페이지 조회
    @GetMapping("/option")
    public String viewSettings(Model model) {
        FileSettings settings = fileSettingsService.getSettings();

        // 바이트를 MB로 변환하여 모델에 추가
        long maxFileSizeMB = settings.getMaxFileSize() / (1024 * 1024);
        model.addAttribute("settings", settings);
        model.addAttribute("maxFileSizeMB", maxFileSizeMB);
        return "admin/manager/settings/settings"; // Thymeleaf 템플릿 경로
    }

    @PostMapping("/option")
    public String updateSettings(@RequestParam("maxFileSizeMB") Long maxFileSizeMB,
                                 @RequestParam("allowedExtensions") String allowedExtensions,
                                 RedirectAttributes redirectAttributes) {
        // 최대 설정 가능 크기 제한 (200MB)
        final long MAX_SETTING_MB = 200;

        if (maxFileSizeMB > MAX_SETTING_MB) {
            Alert alert = new Alert("최대 설정 가능 파일 크기는 " + MAX_SETTING_MB + "MB 입니다.", Alert.AlertType.ERROR);
            redirectAttributes.addFlashAttribute("alert", alert);
            return "redirect:/admin/manager/option";
        }

        // 바이트 단위로 변환
        long maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;

        // FileSettings 객체 생성 및 설정
        FileSettings settings = new FileSettings();
        settings.setId(1L); // ID 고정
        settings.setMaxFileSize(maxFileSizeBytes);
        settings.setAllowedExtensions(allowedExtensions);
        // 파일 저장 경로는 수정하지 않도록 함
        // settings.setFileDir(fileDir);

        // 설정 업데이트
        fileSettingsService.updateSettings(settings);

        Alert alert = new Alert("설정이 성공적으로 업데이트되었습니다.", Alert.AlertType.SUCCESS);
        redirectAttributes.addFlashAttribute("alert", alert);
        return "redirect:/admin/manager/option";
    }

}
