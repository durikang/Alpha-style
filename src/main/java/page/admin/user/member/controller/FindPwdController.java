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

    // 이메일 인증 코드 발송
    @PostMapping("/send-email")
    @ResponseBody
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        try {
            authService.validateAndSendCode(email); // 이메일 검증 및 인증 코드 발송
            return ResponseEntity.ok("이메일로 인증 코드를 전송했습니다.");
        } catch (IllegalArgumentException e) {
            log.error("이메일 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
        }
    }

    // 인증 코드 검증
    @PostMapping("/verify-code")
    @ResponseBody
    public ResponseEntity<String> verifyAuthCode(@RequestParam String email, @RequestParam String authCode) {
        try {
            boolean isVerified = authService.verifyCode(email, authCode);
            if (isVerified) {
                return ResponseEntity.ok("인증 코드 확인 완료");
            } else {
                return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            log.error("인증 코드 검증 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 코드 검증 실패");
        }
    }

    // 비밀번호 재설정
    @PostMapping("/reset-pwd")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(email, newPassword); // 비밀번호 재설정
            redirectAttributes.addFlashAttribute("alert", new Alert("비밀번호가 성공적으로 변경되었습니다.", Alert.AlertType.SUCCESS));
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            log.error("비밀번호 재설정 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alert", new Alert("비밀번호 재설정 중 오류가 발생했습니다.", Alert.AlertType.ERROR));
            return "user/member/find/find-pwd";
        }
    }
}

