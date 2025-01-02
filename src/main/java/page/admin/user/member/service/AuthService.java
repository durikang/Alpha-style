package page.admin.user.member.service;

public interface AuthService {
    void sendVerificationEmail(String email); // 인증 이메일 발송
    boolean verifyCode(String email, String code); // 인증 코드 검증
    String findUserIdByEmail(String email); // 이메일로 아이디 찾기 // 인증 코드 검증

    void validateAndSendCode(String email);
}
