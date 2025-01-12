package page.admin.admin.manager.service;

import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.manager.domain.Logo;

import java.util.List;

public interface LogoService {

    /**
     * 활성화된 로고 경로를 반환합니다.
     */
    String getActiveLogoPath();

    /**
     * 로고를 업로드하고 저장합니다.
     */
    Logo uploadLogo(MultipartFile file);

    /**
     * 모든 로고를 반환합니다.
     */
    List<Logo> getAllLogos();

    /**
     * 로고를 저장합니다. (이름과 파일 기반)
     */
    Logo saveLogo(String logoName, MultipartFile file);

    /**
     * 로고를 삭제합니다.
     */
    void deleteLogo(Long logoId);
}
