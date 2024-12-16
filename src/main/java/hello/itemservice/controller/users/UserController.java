package hello.itemservice.controller.users;

import hello.itemservice.domain.users.Member;
import hello.itemservice.domain.users.MemberRepository;
import hello.itemservice.domain.utils.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user/users")
@RequiredArgsConstructor
public class UserController {

    private final MemberRepository memberRepository;

    // 사용자 목록 조회
    @GetMapping
    public String listUsers(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "user/userList"; // 사용자 목록 페이지로 이동
    }

    // 사용자 상세 조회
    @GetMapping("/{userNo}")
    public String viewUser(@PathVariable("userNo") Long userNo, @ModelAttribute("alert") Alert alert, Model model) {
        Member member = memberRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID: " + userNo));
        model.addAttribute("member", member);
        
        return "user/userDetail"; // 사용자 상세 페이지로 이동
    }

    // 사용자 등록 폼
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("member", new Member());

        return "user/userForm"; // 사용자 등록 폼 페이지로 이동
    }

    // 사용자 등록 처리
    @PostMapping("/add")
    public String addUser(@Validated @ModelAttribute Member member,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("userNo", savedMember.getUserNo());
        redirectAttributes.addFlashAttribute("alert", new Alert("사용자",true));
        return "redirect:/user/users/{userNo}"; // 사용자 상세 페이지로 리다이렉트
    }

    // 사용자 수정 폼
    @GetMapping("/{userNo}/edit")
    public String editForm(@PathVariable("userNo") Long userNo, Model model) {
        Member member = memberRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID: " + userNo));
        model.addAttribute("member", member);
        return "user/userEditForm"; // 사용자 수정 폼 페이지로 이동
    }

    // 사용자 수정 처리
    @PostMapping("/{userNo}/edit")
    public String editUser(@PathVariable("userNo") Long userNo, @ModelAttribute Member member) {
        memberRepository.update(userNo, member);
        return "redirect:/user/users/{userNo}"; // 사용자 상세 페이지로 리다이렉트
    }

    // 사용자 삭제
    @PostMapping("/{userNo}/delete")
    public String deleteUser(@PathVariable("userNo") Long userNo) {
        memberRepository.delete(userNo);
        return "redirect:/user/users"; // 사용자 목록 페이지로 리다이렉트
    }
}
