package page.admin.user.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.email.CodeGenerator;
import page.admin.common.utils.email.EmailService;
import page.admin.user.member.domain.VerificationCode;
import page.admin.user.member.repository.VerificationCodeRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        if (!memberRepository.existsByEmail(email)) {
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
    public boolean verifyCode(String email, String code) {
        System.out.println("Verifying code for email: " + email); // 디버깅 로그
        System.out.println("Received code: " + code);             // 디버깅 로그

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

        System.out.println("Stored code: " + verificationCode.getCode()); // 저장된 코드 확인

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodeRepository.delete(verificationCode);
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        boolean isVerified = verificationCode.getCode().equals(code);

        if (isVerified) {
            verificationCodeRepository.delete(verificationCode);
        }
        return isVerified;
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

        verificationCodeRepository.findByEmail(email).ifPresent(verificationCodeRepository::delete);

        VerificationCode verificationCode = new VerificationCode(email, code, expiryTime);
        verificationCodeRepository.save(verificationCode);

        emailService.sendEmail(email, "인증 코드", "인증 코드는 " + code + "입니다.");
    }
}
