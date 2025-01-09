package page.admin.admin.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.admin.manager.domain.FileSettings;
import page.admin.admin.manager.repository.FileSettingsRepository;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class FileSettingsServiceImpl implements FileSettingsService {

    private final FileSettingsRepository fileSettingsRepository;

    @PostConstruct
    public void init() {
        // 기본 설정이 없으면 초기 설정 생성
        if (!fileSettingsRepository.existsById(1L)) {
            FileSettings defaultSettings = new FileSettings();
            defaultSettings.setId(1L);
            defaultSettings.setMaxFileSize(104857600L); // 100MB (200MB으로 변경 필요)
            defaultSettings.setAllowedExtensions("jpg,png,pdf,docx");
            fileSettingsRepository.save(defaultSettings);
        }
    }

    @Override
    public FileSettings getSettings() {
        return fileSettingsRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("FileSettings not found."));
    }

    @Override
    public void updateSettings(FileSettings settings) {
        // 파일 저장 경로 업데이트 기능 제거
        settings.setId(1L); // 항상 ID=1로 설정
        fileSettingsRepository.save(settings);
    }
}
