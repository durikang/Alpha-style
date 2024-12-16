package hello.itemservice.file;

import hello.itemservice.domain.item.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    private static final int MAX_THUMBNAILS = 4; // 최대 썸네일 수


    // 파일 저장 경로 반환
    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    /**
     * 다중 파일 저장
     * @param multipartFiles 업로드된 파일 리스트
     * @return 저장된 파일 리스트
     * @throws IOException 파일 저장 중 예외 발생
     */
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

    /**
     * 단일 파일 저장
     * @param multipartFile 업로드된 파일
     * @return 저장된 파일 정보
     * @throws IOException 파일 저장 중 예외 발생
     */
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        // 원본 파일명과 저장용 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 실제 파일 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        // 저장된 파일 정보 반환
        return new UploadFile(originalFilename, storeFileName);
    }

    /**
     * 파일명 생성
     * UUID 기반으로 저장용 파일명을 생성
     * @param originalFilename 원본 파일명
     * @return 저장용 파일명
     */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename); // 확장자 추출
        String uuid = UUID.randomUUID().toString(); // UUID 생성
        return uuid + "." + ext; // UUID.확장자 형태로 저장
    }

    /**
     * 확장자 추출
     * @param originalFilename 원본 파일명
     * @return 확장자 (예: png, jpg)
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
