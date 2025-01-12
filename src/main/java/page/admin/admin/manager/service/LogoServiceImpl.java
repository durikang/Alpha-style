package page.admin.admin.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.manager.domain.Logo;
import page.admin.admin.manager.repository.LogoRepository;
import page.admin.common.utils.exception.FileProcessingException;
import page.admin.common.utils.file.FileStore;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoServiceImpl implements LogoService {

    private final LogoRepository logoRepository;
    private final FileStore fileStore;

    private static final String DEFAULT_LOGO_PATH = "/user/img/alpha_Style.png"; // 기본 로고 경로

    @Override
    public String getActiveLogoPath() {
        return logoRepository.findActiveLogo()
                .map(logo -> "/files/" + logo.getStoredFileName())
                .orElse(DEFAULT_LOGO_PATH); // 활성화된 로고가 없으면 기본 경로 반환
    }

    @Override
    public Logo uploadLogo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 기존 활성화된 로고 비활성화
        logoRepository.findActiveLogo().ifPresent(activeLogo -> {
            activeLogo.setActive(false);
            logoRepository.save(activeLogo);
        });

        // 파일 저장
        try {
            var uploadFile = fileStore.storeFile(file);
            Logo logo = new Logo();
            logo.setOriginalFileName(uploadFile.getUploadFileName());
            logo.setStoredFileName(uploadFile.getStoreFileName());
            logo.setActive(true);
            return logoRepository.save(logo);
        } catch (FileProcessingException e) {
            throw new IllegalStateException("로고 업로드 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Logo> getAllLogos() {
        return logoRepository.findAll();
    }

    @Override
    public Logo saveLogo(String logoName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일 저장
        try {
            var uploadFile = fileStore.storeFile(file);
            Logo logo = new Logo();
            logo.setOriginalFileName(uploadFile.getUploadFileName());
            logo.setStoredFileName(uploadFile.getStoreFileName());
            logo.setActive(false); // 기본적으로 비활성화 상태로 저장
            logo.setLogoName(logoName);
            return logoRepository.save(logo);
        } catch (FileProcessingException e) {
            throw new IllegalStateException("로고 저장 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteLogo(Long logoId) {
        Logo logo = logoRepository.findById(logoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 로고가 존재하지 않습니다. ID: " + logoId));

        // 파일 삭제
        if (!fileStore.deleteFile(logo.getStoredFileName())) {
            throw new IllegalStateException("로고 파일 삭제 실패");
        }

        // 로고 데이터 삭제
        logoRepository.delete(logo);
    }
}
