package page.admin.user.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.service.MemberService;
import page.admin.common.utils.Alert;
import page.admin.common.utils.email.EmailService;
import page.admin.user.member.domain.SecurityQuestion;
import page.admin.user.member.domain.dto.GeneralSignupDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user/signup")
@RequiredArgsConstructor
public class GeneralSignupController {

    private final MemberService memberService;
    private final EmailService emailService; // 이메일 전송 서비스

    // 일반 회원 가입 폼
    @GetMapping("/general")
    public String generalSignupForm(Model model) {
        model.addAttribute("signupForm", new GeneralSignupDto()); // 항상 추가
        model.addAttribute("securityQuestions", Arrays.stream(SecurityQuestion.values())
                .map(SecurityQuestion::getQuestion)
                .toList());

        return "user/member/signup/general-signup";
    }

    @PostMapping("/general")
    public String generalSignup(
            @Valid GeneralSignupDto signupForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.debug("Processing signupForm: {}", signupForm);

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            model.addAttribute("signupForm", signupForm);
            model.addAttribute("alert", new Alert(errorMessages, Alert.AlertType.ERROR));
            return "user/member/signup/general-signup";
        }

        // 비밀번호 확인 로직
        if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
            log.warn("Passwords do not match.");
            bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("signupForm", signupForm);
            model.addAttribute("alert", new Alert("비밀번호가 일치하지 않습니다.", Alert.AlertType.WARNING));
            return "user/member/signup/general-signup";
        }

        // 중복 아이디 확인 로직
        if (memberService.findByUserId(signupForm.getUserId()).isPresent()) {
            log.warn("Duplicate userId: {}", signupForm.getUserId());
            bindingResult.rejectValue("userId", "error.userId", "이미 사용 중인 아이디입니다.");
            model.addAttribute("signupForm", signupForm);
            model.addAttribute("alert", new Alert("이미 사용 중인 아이디입니다.", Alert.AlertType.ERROR));
            return "user/member/signup/general-signup";
        }

        // 회원 가입 처리
        try {
            memberService.saveMember(mapToMember(signupForm));
            log.info("Signup successful for userId: {}", signupForm.getUserId());

            // Alert 객체를 RedirectAttributes에 추가
            redirectAttributes.addFlashAttribute("alert", new Alert("회원 가입이 성공적으로 완료되었습니다.", Alert.AlertType.SUCCESS));
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error during signup process", e);
            model.addAttribute("signupForm", signupForm);
            model.addAttribute("alert", new Alert("회원 가입 중 오류가 발생했습니다.", Alert.AlertType.ERROR));
            return "user/member/signup/general-signup";
        }
    }





    // 아이디 중복 확인
    @GetMapping("/check-duplicate-id")
    @ResponseBody
    public boolean checkDuplicateId(String userId) {
        return memberService.findByUserId(userId).isPresent(); // true면 중복, false면 사용 가능
    }

    // 아이디 찾기 폼
    @GetMapping("/find-id")
    public String findIdForm(Model model) {
        return "user/member/find/find-id"; // 아이디 찾기 페이지
    }

    // 아이디 찾기 처리
    @PostMapping("/find-id")
    @ResponseBody
    public ResponseEntity<String> findId(@RequestParam String email) {
        log.debug("Finding ID for email: {}", email);

        // 이메일로 등록된 사용자 조회
        List<Member> members = memberService.findByEmail(email);

        if (members.isEmpty()) {
            log.warn("No members found with email: {}", email);
            return ResponseEntity.badRequest().body("등록된 이메일이 없습니다.");
        }

        // 첫 번째 회원 아이디 가져오기 (여러 회원이 동일 이메일 사용 가능 시 주의)
        Member member = members.get(0);
        String userId = member.getUserId();
        String maskedUserId = maskUserId(userId); // 아이디 마스킹

        // 이메일로 인증 메일 전송
        String emailMessage = "안녕하세요, 고객님. 요청하신 아이디는 다음과 같습니다: " + maskedUserId;
        try {
            emailService.sendEmail(email, "아이디 찾기 결과", emailMessage);
            log.info("Email sent to: {}", email);
            return ResponseEntity.ok("입력한 이메일로 아이디 정보를 전송했습니다.");
        } catch (Exception e) {
            log.error("Error sending email to: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 중 오류가 발생했습니다.");
        }
    }

    // 아이디 마스킹 메서드
    private String maskUserId(String userId) {
        int visibleLength = userId.length() / 2; // 아이디 절반만 표시
        return userId.substring(0, visibleLength) + "*".repeat(Math.max(0, userId.length() - visibleLength));
    }





    private Member mapToMember(GeneralSignupDto signupForm) {
        Member member = new Member();
        member.setUserId(signupForm.getUserId());
        member.setUsername(signupForm.getUsername());
        member.setPassword(signupForm.getPassword()); // TODO: 비밀번호 암호화 추가
        member.setEmail(signupForm.getEmail());
        member.setMobilePhone(signupForm.getMobilePhone());
        member.setZipCode(signupForm.getZipCode());
        member.setAddress(signupForm.getAddress());
        member.setSecondaryAddress(signupForm.getSecondaryAddress());
        member.setBirthDate(LocalDate.parse(signupForm.getBirthDate()));
        member.setSecurityQuestion(signupForm.getSecurityQuestion());
        member.setSecurityAnswer(signupForm.getSecurityAnswer());
        member.setRole("MEMBER");
        member.setGender(signupForm.getGender()); // 추가된 라인
        return member;
    }




}
