package page.admin.order.controller;

import org.springframework.data.domain.Page;
import page.admin.item.domain.DeliveryCode;
import page.admin.item.service.DeliveryCodeService;
import page.admin.item.service.ItemTypeService;
import page.admin.item.service.RegionService;
import page.admin.member.domain.Member;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetail;
import page.admin.order.domain.OrderDetailDTO;
import page.admin.order.domain.dto.OrderSummaryDTO;
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
    private final RegionService regionService;
    private final DeliveryCodeService deliveryCodeService;

    @GetMapping("/list")
    public String getOrderList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "orderDate") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword,
            Model model) {

        // 페이징, 정렬, 검색 처리
        Page<Order> orders = orderService.getOrdersWithSearchAndPaging(searchKeyword, page, size, sortBy, sortDir);

        // 모델에 데이터 추가
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("currentPage", orders.getNumber());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("searchKeyword", searchKeyword);

        return "order/orderList"; // 주문 리스트 HTML
    }



    // 제품별 주문 현황 페이지
    @GetMapping("/summaryList")
    public String getOrderSummaries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "itemId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Page<OrderSummaryDTO> summaries = orderService.getOrderSummariesWithPaging(page, size, sortBy, sortDir);

        model.addAttribute("orderSummaries", summaries.getContent());
        model.addAttribute("currentPage", summaries.getNumber());
        model.addAttribute("totalPages", summaries.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "order/orderSummaryList";
    }






    // 주문 상세 페이지
    @GetMapping("/details/{id}")
    public String getOrderDetails(@PathVariable("id") Long id, Model model) {
        // 주문 정보
        Order order = orderService.getOrderById(id);
        if (order == null) {
            throw new IllegalArgumentException("해당 주문 정보를 찾을 수 없습니다.");
        }

        // 주문자 정보
        Member customer = order.getUser();

        // 주문 상세 정보
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // 배송 상태 목록 (DeliveryCode의 displayName 사용)
        List<String> deliveryStatuses = deliveryCodeService.getAllDeliveryCodes()
                .stream()
                .map(code -> code.getDisplayName())
                .toList();

        // 모델에 데이터 추가
        model.addAttribute("order", order);
        model.addAttribute("customer", customer);
        model.addAttribute("details", orderDetails);
        model.addAttribute("deliveryStatuses", deliveryStatuses);

        return "order/orderDetails";
    }

}
