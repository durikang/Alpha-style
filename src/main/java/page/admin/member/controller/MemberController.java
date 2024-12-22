package page.admin.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.AddForm;
import page.admin.member.domain.dto.MemberList;
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
    public String listUsers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "userNo") String sortField,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") String sortDirection,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // 정렬 설정
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        // 페이지 요청 생성 (정렬 적용)
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 검색 및 페이징 처리
        Page<Member> members = memberService.searchMembers(keyword, sortedPageable);

        // 모델에 데이터 추가
        model.addAttribute("members", members.getContent());
        model.addAttribute("totalPages", members.getTotalPages());
        model.addAttribute("currentPage", members.getNumber() + 1);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "user/userList"; // 뷰 반환
    }




    @GetMapping("/{userNo}")
    public String viewUser(@PathVariable("userNo") Long userNo, @ModelAttribute("alert") Alert alert, Model model) {
        Member member = memberService.getMemberById(userNo);
        model.addAttribute("member", member);
        return "user/userDetail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("addForm", new AddForm());
        return "user/userAddForm";
    }

    // 회원 등록 처리
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("addForm") AddForm addForm,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        // Validation 에러가 있는 경우 다시 등록 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "user/userAddForm";
        }

        // 비밀번호 보안 정책 적용 (나중에 주석 해제 후 구현)
        // validatePassword(addForm.getPassword());

        // AddForm -> Member로 변환
        Member member = new Member();
        member.setUserId(addForm.getUserId());
        member.setUsername(addForm.getUsername());
        member.setPassword(addForm.getPassword()); // 나중에 암호화 필요
        member.setEmail(addForm.getEmail());
        member.setMobilePhone(addForm.getMobilePhone());
        member.setAddress(addForm.getAddress());
        member.setSecondaryAddress(addForm.getSecondaryAddress());
        member.setZipCode(addForm.getZipCode());
        member.setRole(addForm.getRole());


        Member savedMember;

        try {
            // 회원 저장
            savedMember = memberService.saveMember(member);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("userId", "duplicate.userId", "이미 존재하는 아이디입니다.");
            return "user/userAddForm";
        }

        // 알림 메시지 설정 및 리다이렉션
        redirectAttributes.addAttribute("userNo", savedMember.getUserNo());
        redirectAttributes.addFlashAttribute("alert", new Alert("사용자가 성공적으로 등록되었습니다.", Alert.AlertType.SUCCESS));
        return "redirect:/user/users/{userNo}";
    }

    // 비밀번호 보안 정책 검증 (추후 구현 예정)
    // private void validatePassword(String password) {
    //     // 예: 최소 8자, 대소문자 및 특수문자 포함 여부 검사
    //     if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$")) {
    //         throw new IllegalArgumentException("비밀번호는 최소 8자 이상, 대소문자 및 특수문자를 포함해야 합니다.");
    //     }
    // }

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
    public String deleteUser(@PathVariable("userNo") Long userNo, RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteMember(userNo);
            redirectAttributes.addFlashAttribute("alert", new Alert("회원이 성공적으로 삭제되었습니다.", Alert.AlertType.SUCCESS));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("alert", new Alert("현재 회원에 등록된 제품이 있어 회원 탈퇴를 할 수 없습니다. 회원이 등록한 제품을 먼저 삭제한 후 탈퇴를 진행해주세요.", Alert.AlertType.ERROR));
        }
        return "redirect:/user/users"; // 리스트로 리다이렉트
    }

    @PostMapping("/batch-delete")
    @ResponseBody
    public ResponseEntity<Alert> batchDeleteUsers(@RequestBody List<Long> userIds) {
        try {
            for (Long userId : userIds) {
                memberService.deleteMember(userId);
            }
            // 성공 메시지와 함께 Alert 객체 반환
            return ResponseEntity.ok(new Alert("선택한 회원이 성공적으로 삭제되었습니다.", Alert.AlertType.SUCCESS));
        } catch (Exception e) {
            // 에러 메시지와 함께 Alert 객체 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Alert("회원 삭제 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR));
        }
    }


}
