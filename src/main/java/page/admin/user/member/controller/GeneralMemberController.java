package page.admin.user.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
public class GeneralMemberController {

    @GetMapping("/user/order-check")
    public String viewOrderCheck(HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 정보 가져오기
        LoginSessionInfo loginMember = (LoginSessionInfo) session.getAttribute("loginMember");

        // 세션 정보가 없을 경우 (인터셉터에서 이미 처리)
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 실제 구현 시, 주문 데이터를 서비스 레이어를 통해 가져와 모델에 추가
        // 현재는 임시 데이터 사용
        // 예시:
        // List<OrderDto> orders = orderService.getOrdersByUserId(loginMember.getUserId());
        // model.addAttribute("orders", orders);

        // 임시로 주문 조회 페이지로 이동
        return "user/member/order/orderView";
    }

    // 마이페이지 매핑 메서드 추가
    @GetMapping("/user/mypage")
    public String viewMyPage(HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 정보 가져오기
        LoginSessionInfo loginMember = (LoginSessionInfo) session.getAttribute("loginMember");

        // 세션 정보가 없을 경우 (인터셉터에서 이미 처리)
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 실제 구현 시, 마이페이지 데이터를 서비스 레이어를 통해 가져와 모델에 추가
        // 현재는 임시 데이터 사용

        // 예시:
        // UserProfileDto userProfile = userService.getUserProfile(loginMember.getUserId());
        // model.addAttribute("userProfile", userProfile);

        // 임시로 마이페이지 조회 페이지로 이동
        return "user/member/order/mypage";
    }
}
