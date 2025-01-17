package page.admin.user.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.common.utils.Alert;
import page.admin.user.member.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/member/find")
@RequiredArgsConstructor
@Slf4j
public class FindPwdController {

    private final AuthService authService;

    // 비밀번호 찾기 페이지
    @GetMapping("/pwd")
    public String findPwdForm() {
        return "user/member/find/find-pwd"; // 비밀번호 찾기 페이지
    }

    // 비밀번호 찾기용 이메일 인증 코드 발송
    @PostMapping("/send-email")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendEmail(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            authService.validateAndSendCode(email); // 비밀번호 찾기용 인증 코드 발송
            response.put("message", "비밀번호 재설정을 위한 인증 코드가 이메일로 발송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("이메일 검증 실패: {}", e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생: {}", e.getMessage());
            response.put("message", "이메일 전송 실패. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 비밀번호 찾기용 인증 코드 검증
    @PostMapping("/verify-code")
    @ResponseBody
    public ResponseEntity<String> verifyAuthCode(@RequestParam String email, @RequestParam String authCode) {
        try {
            boolean isVerified = authService.verifyCode(email, authCode);
            if (isVerified) {
                return ResponseEntity.ok("인증 코드 확인 완료. 비밀번호 재설정을 진행해주세요.");
            } else {
                return ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            log.error("인증 코드 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("인증 코드 검증 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 코드 확인 중 문제가 발생했습니다.");
        }
    }

    // 비밀번호 재설정
    @PostMapping("/reset-pwd")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(email, newPassword); // 비밀번호 재설정
            redirectAttributes.addFlashAttribute("alert", new Alert("비밀번호가 성공적으로 변경되었습니다.", Alert.AlertType.SUCCESS));
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 재설정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alert", new Alert(e.getMessage(), Alert.AlertType.ERROR));
            return "user/member/find/find-pwd";
        } catch (Exception e) {
            log.error("비밀번호 재설정 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alert", new Alert("비밀번호 재설정 중 문제가 발생했습니다.", Alert.AlertType.ERROR));
            return "user/member/find/find-pwd";
        }
    }
}
