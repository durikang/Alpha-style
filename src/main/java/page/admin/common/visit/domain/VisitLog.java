package page.admin.common.visit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_log")
@Getter
@Setter
@NoArgsConstructor
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;
    private String userAgent;
    private LocalDateTime visitTime;

    // 생성자
    public VisitLog(String ip, String userAgent) {
        this.ip = ip;
        this.userAgent = userAgent;
        this.visitTime = LocalDateTime.now();
    }
}
