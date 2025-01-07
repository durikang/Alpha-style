package page.admin.admin.item.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upload_file_seq")
    private Long id;

    private String uploadFileName;
    private String storeFileName;

    @CreationTimestamp
    private LocalDateTime uploadTime; // 업로드 시간

    // 기존 생성자
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    // URL만 사용하는 생성자 (필요에 따라 수정 또는 제거)
    public UploadFile(String url) {
        this.uploadFileName = url;
        this.storeFileName = url;
    }
}
