package page.admin.user.member.service;

public interface AuthService {
    void validateAndSendCodeForSignup(String email); // 회원가입용
    void validateAndSendCodeForFindId(String email); // 아이디 찾기용
    boolean verifyCode(String email, String code); // 인증 코드 검증
    void resetPassword(String email, String newPassword); // 비밀번호 재설정
    void validateAndSendCode(String email); // 비밀번호 찾기용
}
