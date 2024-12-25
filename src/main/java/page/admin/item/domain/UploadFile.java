package page.admin.item.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@SequenceGenerator(
        name = "upload_file_seq_generator",
        sequenceName = "upload_file_seq", // Oracle 시퀀스 이름
        allocationSize = 1
)
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upload_file_seq_generator")
    @SequenceGenerator(name = "upload_file_seq_generator", sequenceName = "upload_file_seq", allocationSize = 1)
    private Long id;


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
