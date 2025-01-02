package page.admin.user.member.service;

public interface AuthService {
    void sendVerificationEmail(String email);
    boolean verifyCode(String email, String code);
    String findUserIdByEmail(String email);
    void validateAndSendCode(String email);

    // 새 메서드 추가
    void resetPassword(String email, String newPassword);
}
