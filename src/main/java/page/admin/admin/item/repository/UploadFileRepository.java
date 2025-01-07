package page.admin.admin.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.admin.item.domain.UploadFile;

import java.time.LocalDateTime;
import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    List<UploadFile> findByUploadFileNameContaining(String keyword);
    List<UploadFile> findByUploadTimeBetween(LocalDateTime start, LocalDateTime end);
}
