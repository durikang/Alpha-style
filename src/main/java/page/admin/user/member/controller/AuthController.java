package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import page.admin.user.member.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입용 이메일 인증 코드 발송
    @PostMapping("/signup/send-code")
    public ResponseEntity<Map<String, String>> sendCodeForSignup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println("Received email for signup: " + email); // 디버깅 로그
        Map<String, String> response = new HashMap<>();
        try {
            authService.validateAndSendCodeForSignup(email);
            response.put("message", "회원가입용 인증 코드가 이메일로 발송되었습니다!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



    // 아이디 찾기용 이메일 인증 코드 발송
    @PostMapping("/find-id/send-code")
    public ResponseEntity<Map<String, String>> sendCodeForFindId(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            authService.validateAndSendCodeForFindId(email);
            response.put("message", "아이디 찾기용 인증 코드가 이메일로 발송되었습니다!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 비밀번호 찾기용 이메일 인증 코드 발송
    @PostMapping("/reset-password/send-code")
    public ResponseEntity<Map<String, String>> sendCodeForPasswordReset(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            authService.validateAndSendCodeForPasswordReset(email);
            response.put("message", "비밀번호 재설정용 인증 코드가 이메일로 발송되었습니다!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        System.out.println("Received email: " + email); // 디버깅 로그
        System.out.println("Received code: " + code);   // 디버깅 로그

        try {
            boolean isVerified = authService.verifyCode(email, code);
            return isVerified ? ResponseEntity.ok("인증 성공!") : ResponseEntity.badRequest().body("인증 실패!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }

}
