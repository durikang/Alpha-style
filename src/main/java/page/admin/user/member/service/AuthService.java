package page.admin.user.member.service;

public interface AuthService {
    void sendVerificationEmail(String email); // 인증 이메일 발송
    boolean verifyCode(String email, String code); // 인증 코드 검증
}
