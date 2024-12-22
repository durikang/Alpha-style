package page.admin.item.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;

    // 기존 생성자
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    // URL만 사용하는 생성자 추가
    public UploadFile(String url) {
        this.uploadFileName = url; // 업로드 파일명에도 URL 설정
        this.storeFileName = url; // 저장 파일명에도 URL 설정
    }
}
