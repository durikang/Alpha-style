package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import page.admin.common.utils.Alert;
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
        System.out.println("Received email: " + email); // 디버깅 로그
        Map<String, String> response = new HashMap<>();
        try {
            authService.validateAndSendCodeForFindId(email);
            response.put("message", "아이디 찾기용 인증 코드가 이메일로 발송되었습니다!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation failed: " + e.getMessage()); // 에러 로그
            response.put("message", e.getMessage()); // 클라이언트로 반환
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

    /**
     * 비밀번호 재설정
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        try {
            // 실제 비밀번호 재설정 로직 (이미 AuthServiceImpl에 있음)
            authService.resetPassword(email, newPassword);

            response.put("success", true);
            response.put("message", "비밀번호가 성공적으로 재설정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // JSON 요청 처리용 인증 코드 확인
    @PostMapping("/verify-code-json")
    public ResponseEntity<Map<String, Object>> verifyCodeWithJson(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        Map<String, Object> response = new HashMap<>();

        try {
            // 인증 성공 여부
            boolean isVerified = authService.verifyCode(email, code);

            if (isVerified) {
                // 아이디 찾기 로직 (예: email로 아이디를 조회)
                // userId가 있다면 아래와 같이 내려줌
                String userId = authService.findUserIdByEmail(email);

                response.put("success", true);
                response.put("userId", userId);
                response.put("message", "인증 성공!");
                return ResponseEntity.ok(response);

            } else {
                response.put("success", false);
                response.put("message", "인증 실패!");
                // userId는 굳이 내려줄 필요 없음
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
