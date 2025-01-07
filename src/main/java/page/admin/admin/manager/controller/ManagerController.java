package page.admin.admin.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import page.admin.admin.manager.domain.FileSettings;
import page.admin.admin.manager.service.FileSettingsService;

@Controller
@RequestMapping("/admin/manager")
@RequiredArgsConstructor
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

    // 설정 업데이트
    @PostMapping("/option")
    public String updateSettings(@RequestParam("maxFileSizeMB") Long maxFileSizeMB,
                                 @RequestParam("allowedExtensions") String allowedExtensions,
                                 Model model) {
        // 바이트 단위로 변환
        long maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;

        // FileSettings 객체 생성 및 설정
        FileSettings settings = new FileSettings();
        settings.setId(1L); // ID 고정
        settings.setMaxFileSize(maxFileSizeBytes);
        settings.setAllowedExtensions(allowedExtensions);
        // 파일 저장 경로는 수정하지 않도록 함
        // settings.setFileDir(fileDir);

        // 파일 저장 경로 유효성 검증 제거
        // if (!settings.isValidFileDir()) {
        //     model.addAttribute("settings", settings);
        //     model.addAttribute("error", "유효하지 않은 파일 저장 경로입니다. 올바른 절대 경로를 입력하세요.");
        //     return "admin/manager/settings/settings";
        // }

        // 설정 업데이트
        fileSettingsService.updateSettings(settings);
        model.addAttribute("settings", settings);
        model.addAttribute("message", "설정이 성공적으로 업데이트되었습니다.");
        return "admin/manager/settings/settings";
    }
}
