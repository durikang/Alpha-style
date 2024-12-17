package page.admin.log.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "system_log")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_log_seq")
    @SequenceGenerator(name = "system_log_seq", sequenceName = "system_log_seq", allocationSize = 1)
    private Long logId; // 로그 ID

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName; // 엔티티 이름 (예: Item, Member)

    @Column(name = "entity_id")
    private Long entityId; // 대상 엔티티 ID

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // 작업 타입 (등록, 수정, 삭제)

    @Column(name = "performed_by", nullable = false, length = 50)
    private String performedBy; // 작업한 사용자 ID 또는 이름

    @Column(name = "performed_date", nullable = false)
    private LocalDateTime performedDate; // 작업 일시

    @Column(name = "log_details", length = 1000)
    private String details; // 변경된 내용 설명

    public SystemLog(String entityName, Long entityId, String actionType, String performedBy, String details) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.actionType = actionType;
        this.performedBy = performedBy;
        this.performedDate = LocalDateTime.now();
        this.details = details;
    }
}
