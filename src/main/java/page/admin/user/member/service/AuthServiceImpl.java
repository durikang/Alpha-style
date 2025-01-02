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
    public void sendVerificationEmail(String email) {
        String code = CodeGenerator.generateCode();
        LocalDateTime expiryTime = LocalDateTime.now().plus(5, ChronoUnit.MINUTES); // 5분 유효 시간

        // 기존 인증 코드 삭제
        verificationCodeRepository.findByEmail(email).ifPresent(verificationCodeRepository::delete);

        // 새 인증 코드 저장
        VerificationCode verificationCode = new VerificationCode(email, code, expiryTime);
        verificationCodeRepository.save(verificationCode);

        // 이메일 발송 로직 호출
        emailService.sendEmail(email, "인증 코드", "인증 코드는 " + code + "입니다.");
    }

    @Override
    public boolean verifyCode(String email, String code) {
        var verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

        // 만료 시간 검증
        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodeRepository.delete(verificationCode); // 만료된 코드 삭제
            throw new IllegalStateException("인증 코드가 만료되었습니다.");
        }

        boolean isVerified = verificationCode.getCode().equals(code);

        // 인증 성공 시, 인증 코드 삭제
        if (isVerified) {
            verificationCodeRepository.delete(verificationCode);
        }

        return isVerified;
    }

    @Override
    public String findUserIdByEmail(String email) {
        return memberRepository.findSingleMemberByEmail(email)
                .map(Member::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
    }

    @Override
    public void validateAndSendCode(String email) {
        // 이메일 존재 여부 확인
        boolean emailExists = memberRepository.existsByEmail(email);
        if (!emailExists) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다. 회원 가입 후 다시 시도해주세요.");
        }

        // 이메일로 인증 코드 발송
        sendVerificationEmail(email);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // 이메일로 사용자 검색
        Member member = memberRepository.findSingleMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 비밀번호 재설정
        member.setPassword(newPassword); // TODO: 비밀번호 암호화 추가
        memberRepository.save(member);
    }


}
