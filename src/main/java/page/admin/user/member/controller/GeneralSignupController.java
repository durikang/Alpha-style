package page.admin.user.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.service.MemberService;
import page.admin.common.utils.Alert;
import page.admin.user.member.domain.SecurityQuestion;
import page.admin.user.member.domain.dto.GeneralSignupDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user/signup")
@RequiredArgsConstructor
public class GeneralSignupController {

    private final MemberService memberService;

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
        return member;
    }



}
