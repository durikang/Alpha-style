// UploadFileService.java
package page.admin.admin.item.service;

import page.admin.admin.item.domain.UploadFile;

import java.time.LocalDateTime;
import java.util.List;

public interface UploadFileService {
    List<UploadFile> searchFilesByName(String keyword);
    List<UploadFile> searchFilesByDateRange(LocalDateTime start, LocalDateTime end);
    void deleteFileById(Long id);
    UploadFile getFileById(Long id);
}
