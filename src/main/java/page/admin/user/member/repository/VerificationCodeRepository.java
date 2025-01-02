package page.admin.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.user.member.domain.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);
}