package page.admin.admin.item.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@SequenceGenerator(
        name = "upload_file_seq_generator",
        sequenceName = "upload_file_seq", // Oracle 시퀀스 이름
        allocationSize = 1
)
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upload_file_seq_generator")
    private Long id;

    private String uploadFileName;
    private String storeFileName;

    @CreationTimestamp
    private LocalDateTime uploadTime; // 업로드 시간

    // 추가된 필드: Item과의 다대일 관계
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

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
