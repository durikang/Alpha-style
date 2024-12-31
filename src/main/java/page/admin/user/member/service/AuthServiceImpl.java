package page.admin.user.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.common.utils.email.CodeGenerator;
import page.admin.common.utils.email.EmailService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final Map<String, String> verificationCodes = new HashMap<>(); // 이메일-코드 매핑

    @Override
    public void sendVerificationEmail(String email) {
        String code = CodeGenerator.generateCode(); // 6자리 인증 코드 생성
        verificationCodes.put(email, code); // 이메일과 코드 매핑 저장
        String subject = "회원가입 인증 코드";
        String text = "회원가입을 위한 인증 코드는 다음과 같습니다: " + code;
        emailService.sendEmail(email, subject, text); // 이메일 발송
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email); // 저장된 코드 가져오기
        return storedCode != null && storedCode.equals(code); // 코드 일치 여부 확인
    }
}
