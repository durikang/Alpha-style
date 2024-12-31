package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import page.admin.user.member.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        try {
            authService.sendVerificationEmail(email);
            return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("인증 코드 발송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = authService.verifyCode(email, code);
        return isVerified
                ? ResponseEntity.ok("인증 성공!")
                : ResponseEntity.badRequest().body("인증 실패! 올바른 코드를 입력해주세요.");
    }
}
