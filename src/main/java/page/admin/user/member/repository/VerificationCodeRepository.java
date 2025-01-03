package page.admin.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.user.member.domain.VerificationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);
    // 만료된 코드 조회
    List<VerificationCode> findAllByStatusAndExpiryTimeBefore(String status, LocalDateTime time);
}