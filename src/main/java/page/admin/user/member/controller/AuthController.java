package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendCode(
            @RequestParam(required = false) String email,
            @RequestBody(required = false) Map<String, String> requestBody) {
        Map<String, String> response = new HashMap<>();
        try {
            // 요청에서 이메일 추출
            if (email == null && requestBody != null) {
                email = requestBody.get("email");
            }

            // 이메일 검증
            if (email == null || email.isEmpty()) {
                response.put("message", "이메일이 제공되지 않았습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 서비스 계층에서 이메일 검증 및 인증 코드 발송 처리
            authService.validateAndSendCode(email);

            // 성공 응답 메시지
            response.put("message", "인증 코드가 이메일로 발송되었습니다!");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 이메일 존재하지 않는 경우
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // 알 수 없는 오류 발생
            response.put("message", "알 수 없는 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }





    /*
        아이디 찾기용 인증 코드
     */
    @PostMapping("/find-id/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCodeForFindId(
            @RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String code = requestBody.get("code");
        Map<String, Object> response = new HashMap<>();

        try {
            if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
                response.put("success", false);
                response.put("message", "이메일 또는 코드가 제공되지 않았습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isVerified = authService.verifyCode(email, code);
            if (isVerified) {
                // 이메일에 연결된 아이디 조회
                String userId = authService.findUserIdByEmail(email);
                response.put("success", true);
                response.put("userId", userId);
                response.put("message", "인증 성공!");
            } else {
                response.put("success", false);
                response.put("message", "인증 실패! 올바른 코드를 입력해주세요.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "알 수 없는 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /*
        회원 가입용 인증 코드
     */
    @PostMapping("/signup/verify-code")
    public ResponseEntity<String> verifyCodeForSignup(
            @RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String code = requestBody.get("code");

        try {
            if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("이메일 또는 코드가 제공되지 않았습니다.");
            }

            boolean isVerified = authService.verifyCode(email, code);
            return isVerified
                    ? ResponseEntity.ok("인증 성공!")
                    : ResponseEntity.badRequest().body("인증 실패! 올바른 코드를 입력해주세요.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }


}
