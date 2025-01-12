package page.admin.admin.manager.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "LOGO")
public class Logo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logo_seq")
    @SequenceGenerator(name = "logo_seq", sequenceName = "LOGO_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "ORIGINAL_FILE_NAME", nullable = false, length = 255)
    private String originalFileName; // 업로드된 파일의 원래 이름

    @Column(name = "STORED_FILE_NAME", nullable = false, length = 255)
    private String storedFileName;   // 저장된 파일 이름

    @Column(name = "LOGO_NAME", length = 100)
    private String logoName;         // 로고의 이름 (옵션)

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;          // 현재 활성화된 로고인지 여부
}
