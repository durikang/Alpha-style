package page.admin.user.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.service.OrderService;
import page.admin.user.member.domain.dto.LoginSessionInfo;

@Controller
@RequiredArgsConstructor
public class GeneralMemberController {

    private final OrderService orderService;

    @GetMapping("/user/order-check/user/{userId}")
    public String viewOrderCheck(
            @PathVariable("userId") String userId,
            HttpSession session,
            Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortField", defaultValue = "orderDate") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        LoginSessionInfo loginMember = (LoginSessionInfo) session.getAttribute("loginMember");

        if (loginMember == null || !loginMember.getUserId().equals(userId)) {
            return "redirect:/login";
        }

        // 페이지 인덱스 유효성 검증
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> ordersPage = orderService.getOrdersByUserIdWithFilters(
                userId, keyword, pageable, sortField, sortDirection
        );

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("currentPage", ordersPage.getNumber() + 1);
        model.addAttribute("startPage", Math.max(1, ordersPage.getNumber() - 2));
        model.addAttribute("endPage", Math.min(ordersPage.getTotalPages(), ordersPage.getNumber() + 3));
        model.addAttribute("url", "/user/order-check/user/" + userId);
        model.addAttribute("actionUrl", "/user/order-check/user/" + userId);
        model.addAttribute("userId", userId);

        return "user/member/order/orderView";
    }




    @GetMapping("/user/order-check/order/{orderNo}")
    public String viewOrderDetail(
            @PathVariable("orderNo") Long orderNo,
            HttpSession session,
            Model model
    ) {
        LoginSessionInfo loginMember = (LoginSessionInfo) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderNo);

        if (!order.getUser().getUserId().equals(loginMember.getUserId())) {
            return "redirect:/user/order-check";
        }

        model.addAttribute("order", order);

        return "user/member/order/orderDetail";
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
