package page.admin.utils.file;

import page.admin.item.domain.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    private static final int MAX_THUMBNAILS = 4; // 최대 썸네일 수

    // 파일 저장 경로 반환
    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    // 다중 파일 저장
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (int i = 0; i < Math.min(multipartFiles.size(), MAX_THUMBNAILS); i++) {
            MultipartFile multipartFile = multipartFiles.get(i);
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile)); // 각 파일을 저장
            }
        }
        return storeFileResult;
    }

    // 단일 파일 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        // 원본 파일명과 저장용 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 저장 디렉토리 확인 및 생성
        ensureDirectoryExists();

        // 실제 파일 저장
        File targetFile = new File(getFullPath(storeFileName));
        multipartFile.transferTo(targetFile);

        // 저장된 파일 정보 반환
        return new UploadFile(originalFilename, storeFileName);
    }

    // 파일 삭제
    public void deleteFile(String storeFileName) {
        if (storeFileName == null || storeFileName.isEmpty()) {
            return;
        }
        File file = new File(getFullPath(storeFileName));
        if (file.exists()) {
            if (file.delete()) {
                log.info("파일 삭제 성공: {}", file.getAbsolutePath());
            } else {
                log.warn("파일 삭제 실패: {}", file.getAbsolutePath());
            }
        } else {
            log.warn("파일이 존재하지 않아 삭제할 수 없음: {}", file.getAbsolutePath());
        }
    }

    // 파일명 생성
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // 저장 디렉토리 확인 및 생성
    private void ensureDirectoryExists() {
        File directory = new File(fileDir);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            log.info("디렉토리 생성: {}", directory.getPath());
            if (!isCreated) {
                throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다: " + fileDir);
            }
        }
    }
}
