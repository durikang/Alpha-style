package page.admin.admin.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.manager.domain.FileSettings;

public interface FileSettingsRepository extends JpaRepository<FileSettings, Long> {
    // 추가적인 쿼리가 필요하면 여기에 정의
}
