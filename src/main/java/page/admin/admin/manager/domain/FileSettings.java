package page.admin.admin.manager.domain;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "file_settings") // 테이블 이름 지정
public class FileSettings {

    @Id
    private Long id; // 기본 키, 단일 행을 위해 항상 1로 설정

    private Long maxFileSize; // 최대 파일 크기 (바이트 단위)
    private String allowedExtensions; // 허용 확장자 (콤마로 구분, 예: "jpg,png,pdf,docx")

}
