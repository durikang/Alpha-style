package page.admin.user.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String code;
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private String status = "PENDING"; // 초기 상태는 PENDING

    public VerificationCode(String email, String code, LocalDateTime expiryTime) {
        this.email = email;
        this.code = code;
        this.expiryTime = expiryTime;
    }
    public VerificationCode(String email, String code, LocalDateTime expiryTime, String status) {
        this.email = email;
        this.code = code;
        this.expiryTime = expiryTime;
        this.status = status;
    }
}