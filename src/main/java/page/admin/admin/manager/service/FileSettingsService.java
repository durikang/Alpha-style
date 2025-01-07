package page.admin.admin.manager.service;

import page.admin.admin.manager.domain.FileSettings;

public interface FileSettingsService {
    FileSettings getSettings();
    void updateSettings(FileSettings settings);
}
