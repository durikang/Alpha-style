package page.admin.order.controller;

import page.admin.item.domain.DeliveryCode;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetailDTO;
import page.admin.item.repository.DeliveryCodeRepository;
import page.admin.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DeliveryCodeRepository deliveryCodeRepository;

    @ModelAttribute("statuses")
    public List<DeliveryCode> statuses() {
        return deliveryCodeRepository.findAll();
    }

    // 주문 목록 페이지
    @GetMapping("/list")
    public String orderList(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    // 주문 상세 정보
    @GetMapping("/details/{orderNo}")
    public String orderDetails(@PathVariable("orderNo") Long orderNo, Model model) {
        Order order = orderService.getOrderById(orderNo);
        List<OrderDetailDTO> details = orderService.getOrderDetails(orderNo);

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "order/orderDetails";
    }

    // 주문 상태 변경
    @PostMapping("/update")
    public String updateOrderStatus(@RequestParam Long orderNo, @RequestParam String status) {
        orderService.updateOrderStatus(orderNo, status);
        return "redirect:/orders/details/" + orderNo;
    }

    // 대량 상태 변경
    @PostMapping("/changeMultipleStatus")
    public String changeMultipleStatus(@RequestParam List<Long> selectedOrders,
                                       @RequestParam String status) {
        orderService.updateMultipleOrderStatus(selectedOrders, status);
        return "redirect:/orders/statusFilter";
    }
}
