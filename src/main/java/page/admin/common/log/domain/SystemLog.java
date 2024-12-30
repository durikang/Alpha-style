package page.admin.common.log.domain;

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
    private Long logId;

    private String entityName;
    private Long entityId;
    private String actionType;
    private String performedBy;
    private LocalDateTime performedDate;
    private String details;

    public SystemLog(String entityName, Long entityId, String actionType, String performedBy, String details) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.actionType = actionType;
        this.performedBy = performedBy;
        this.performedDate = LocalDateTime.now();
        this.details = details;
    }
}
