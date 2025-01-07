// UploadFileServiceImpl.java
package page.admin.admin.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.admin.item.domain.UploadFile;
import page.admin.admin.item.repository.UploadFileRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {
    private final UploadFileRepository uploadFileRepository;

    @Override
    public List<UploadFile> searchFilesByName(String keyword) {
        return uploadFileRepository.findByUploadFileNameContaining(keyword);
    }

    @Override
    public List<UploadFile> searchFilesByDateRange(LocalDateTime start, LocalDateTime end) {
        return uploadFileRepository.findByUploadTimeBetween(start, end);
    }

    @Override
    public void deleteFileById(Long id) {
        uploadFileRepository.deleteById(id);
    }

    @Override
    public UploadFile getFileById(Long id) {
        return uploadFileRepository.findById(id).orElse(null);
    }

    // 추가적인 검색 메서드 구현 가능
}
