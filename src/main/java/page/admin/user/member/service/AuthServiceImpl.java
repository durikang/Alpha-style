package page.admin.user.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.email.CodeGenerator;
import page.admin.common.utils.email.EmailService;
import page.admin.user.member.domain.VerificationCode;
import page.admin.user.member.repository.VerificationCodeRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public void validateAndSendCodeForSignup(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        sendVerificationEmail(email);
    }

    @Override
    public void validateAndSendCodeForFindId(String email) {
        System.out.println("Validating email: " + email); // 디버깅 로그 추가
        boolean emailExists = memberRepository.existsByEmail(email);
        System.out.println("Email exists: " + emailExists); // 디버깅 로그 추가
        if (!emailExists) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }
        sendVerificationEmail(email);
    }



    @Override
    public void validateAndSendCodeForPasswordReset(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }
        sendVerificationEmail(email);
    }

    @Override
    public void validateAndSendCode(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }
        sendVerificationEmail(email); // 통합된 인증 코드 발송
    }

    @Override
    public String findUserIdByEmail(String email) {
        // 'findSingleMemberByEmail'를 호출하여 해당 Member 엔티티를 찾는다.
        Member member = memberRepository.findSingleMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 찾은 Member의 userId를 리턴
        return member.getUserId();
    }

    @Override
    public boolean verifyCode(String email, String code) {
        System.out.println("Verifying code for email: " + email); // 디버깅 로그
        System.out.println("Received code: " + code);             // 디버깅 로그

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

        System.out.println("Stored code: " + verificationCode.getCode()); // 저장된 코드 확인

        // 만료 시간 확인
        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCode.setStatus("EXPIRED"); // 상태를 만료로 설정
            verificationCodeRepository.save(verificationCode);
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        // 상태가 이미 VERIFIED인 경우
        if ("VERIFIED".equals(verificationCode.getStatus())) {
            throw new IllegalStateException("이미 사용된 인증 코드입니다.");
        }

        boolean isVerified = verificationCode.getCode().equals(code);

        if (isVerified) {
            verificationCode.setStatus("VERIFIED"); // 상태를 검증됨으로 설정
            verificationCodeRepository.save(verificationCode);
        }
        return isVerified;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        List<VerificationCode> expiredCodes = verificationCodeRepository.findAllByStatusAndExpiryTimeBefore("EXPIRED", now);

        if (!expiredCodes.isEmpty()) {
            verificationCodeRepository.deleteAll(expiredCodes);
            System.out.println("Expired verification codes have been cleaned up: " + expiredCodes.size());
        } else {
            System.out.println("No expired verification codes to clean up at " + now);
        }
    }



    @Override
    public void resetPassword(String email, String newPassword) {
        Member member = memberRepository.findSingleMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
        member.setPassword(newPassword); // TODO: 비밀번호 암호화 추가
        memberRepository.save(member);
    }

    private void sendVerificationEmail(String email) {
        String code = CodeGenerator.generateCode();
        LocalDateTime expiryTime = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        
        /*만료된 인증코드 찾는 코드*/
        verificationCodeRepository.findByEmail(email).ifPresent(verificationCodeRepository::delete);

        VerificationCode verificationCode = new VerificationCode(email, code, expiryTime);
        verificationCodeRepository.save(verificationCode);

        emailService.sendEmail(email, "인증 코드", "인증 코드는 " + code + "입니다.");
    }
}
