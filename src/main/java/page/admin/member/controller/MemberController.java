package page.admin.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.UpdateForm;
import page.admin.member.service.MemberService;
import page.admin.utils.Alert;

import java.util.List;

@Controller
@RequestMapping("/user/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String listUsers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "user/userList";
    }

    @GetMapping("/{userNo}")
    public String viewUser(@PathVariable("userNo") Long userNo, @ModelAttribute("alert") Alert alert, Model model) {
        Member member = memberService.getMemberById(userNo);
        model.addAttribute("member", member);
        return "user/userDetail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("member", new Member());
        return "user/userForm";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute Member member, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/userForm";
        }
        Member savedMember = memberService.saveMember(member);
        redirectAttributes.addAttribute("userNo", savedMember.getUserNo());
        redirectAttributes.addFlashAttribute("alert", new Alert("사용자가 등록되었습니다.", true));
        return "redirect:/user/users/{userNo}";
    }

    @GetMapping("/{userNo}/edit")
    public String editForm(@PathVariable("userNo") Long userNo, Model model) {
        // Service에서 UpdateForm 반환
        UpdateForm updateForm = memberService.getUpdateFormById(userNo);

        // 모델에 updateForm 추가
        model.addAttribute("updateForm", updateForm);
        return "user/userEditForm";
    }

    @PostMapping("/{userNo}/edit")
    public String editUser(@PathVariable("userNo") Long userNo,
                           @Valid @ModelAttribute("updateForm") UpdateForm updateForm,
                           BindingResult bindingResult,
                           Model model) {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("updateForm", updateForm);
            return "user/userEditForm";
        }

        // DTO → Entity 변환 및 서비스 호출
        memberService.editMemberInfo(userNo, updateForm);

        return "redirect:/user/users/{userNo}";
    }

    @PostMapping("/{userNo}/delete")
    public String deleteUser(@PathVariable("userNo") Long userNo) {
        memberService.deleteMember(userNo);
        return "redirect:/user/users";
    }
}
