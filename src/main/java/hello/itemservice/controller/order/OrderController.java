package hello.itemservice.controller.order;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.order.*;
import hello.itemservice.domain.users.Member;
import hello.itemservice.domain.users.MemberRepository;
import hello.itemservice.domain.utils.Alert;
import hello.itemservice.domain.utils.CommonDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/64x64.png?text=No+Image";
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    @ModelAttribute("statuses")
    public List<DeliveryCode> statuses() {
        return CommonDataProvider.getDeliveryStatuses();
    }

    // 주문 목록 페이지
    @GetMapping("/list")
    public String orderList(Model model) {
        // 모든 주문 데이터를 조회하여 모델에 추가
        List<Order> orders = orderRepository.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", CommonDataProvider.getDeliveryStatuses()); // 배송 상태 데이터 추가
        model.addAttribute("detailsUrl", "/orders/details/");
        return "order/orderList"; // 주문 목록을 보여주는 뷰 이름
    }

    @GetMapping("/details/{orderNo}")
    public String orderDetails(@PathVariable("orderNo") Long orderNo, Model model) {
        // 1. 주문 정보 조회
        Order order = orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 주문 번호: " + orderNo));

        // 2. 고객 정보 조회
        Member customer = memberRepository.findById(order.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 번호: " + order.getUserNo()));
        model.addAttribute("customer", customer);

        // 3. 주문 상세 정보 DTO 변환
        List<OrderDetailDTO> detailDTOs = order.getDetails().stream()
                .map(detail -> convertToDTO(order, detail))
                .toList();

        // 4. 모델에 주문 및 상세 정보 추가
        model.addAttribute("order", order);         // 주문 정보 추가
        model.addAttribute("statuses", CommonDataProvider.getDeliveryStatuses()); // 배송 상태 데이터 추가
        model.addAttribute("details", detailDTOs); // 주문 상세 정보 추가

        return "order/orderDetails";
    }

    // DTO 변환 메서드
    private OrderDetailDTO convertToDTO(Order order, OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setProductId(detail.getItem().getId());
        dto.setQuantity(detail.getQuantity());
        dto.setItemName(detail.getItem() != null ? detail.getItem().getItemName() : "상품 없음");
        dto.setItemPrice(detail.getItem() != null ? detail.getItem().getPrice() : 0);
        dto.setSubtotal(detail.getSubtotal() != null ? detail.getSubtotal() : 0.0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dto.setOrderDate(order.getOrderDate() != null
                ? order.getOrderDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .format(formatter)
                : "날짜 없음");

        dto.setDelivaryStatus(order.getDelivaryStatus());
        dto.setMainImageUrl(detail.getItem() != null && detail.getItem().getMainImage() != null
                ? detail.getItem().getMainImage().getStoreFileName()
                : DEFAULT_IMAGE_URL);
        return dto;
    }
    


    // 주문 상태 변경
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam Long orderNo, @RequestParam String status) {
        // 특정 주문 번호의 상태를 업데이트
        orderRepository.updateOrderStatus(orderNo, status);
        return "redirect:/orders/list"; // 주문 목록 페이지로 리다이렉트
    }


    @PostMapping("/update")
    public String updateOrder(
            @RequestParam("orderNo") Long orderNo,
            @RequestParam("status") String statusCode,
            RedirectAttributes redirectAttributes) {
        log.debug("Received orderNo: {}", orderNo);
        log.debug("Received status code: {}", statusCode);

        // 주문 정보 확인
        Order order = orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 주문 번호: " + orderNo));

        // Code와 displayName 매핑
        String displayName = CommonDataProvider.getDeliveryStatuses().stream()
                .filter(status -> status.getCode().equals(statusCode))
                .map(DeliveryCode::getDisplayName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 상태 코드: " + statusCode));

        // 주문 상태 업데이트 (displayName으로 저장)
        order.setDelivaryStatus(displayName);

        // 성공 메시지 설정
        redirectAttributes.addFlashAttribute("alert", new Alert("배송 상태가 성공적으로 업데이트되었습니다!", true));

        return "redirect:/orders/details/" + orderNo; // 상세 페이지로 리다이렉트
    }



    // 주문 상태 필터링
    @GetMapping("/statusFilter")
    public String filterOrders(@RequestParam(name = "status",required = false, defaultValue = "전체") String status, Model model) {
        List<Order> filteredOrders;
        if ("전체".equals(status)) {
            filteredOrders = orderRepository.findAll();
        } else {
            filteredOrders = orderRepository.findAll().stream()
                    .filter(order -> order.getDelivaryStatus().equals(status))
                    .toList();
        }
        model.addAttribute("orders", filteredOrders);
        model.addAttribute("status", status);
        return "order/orderStatusManage";
    }

    // 대량 상태 변경
    @PostMapping("/changeMultipleStatus")
    public String changeMultipleStatus(@RequestParam List<Long> selectedOrders, @RequestParam Map<String, String> allParams) {
        for (Long orderNo : selectedOrders) {
            String newStatus = allParams.get("status_" + orderNo);
            orderRepository.updateOrderStatus(orderNo, newStatus);
        }
        return "redirect:/orders/statusFilter";
    }


}
