package page.admin.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.item.service.DeliveryCodeService;
import page.admin.item.service.RegionService;
import page.admin.member.domain.Member;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetail;
import page.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.order.service.OrderService;

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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "orderDate") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "searchKeyword", defaultValue = "") String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // 정렬 설정
        Sort sort;
        try {
            sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortField);
        } catch (IllegalArgumentException e) {
            sort = Sort.by(Sort.Direction.ASC, sortField); // 기본값
        }

        // 페이지 요청 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 검색 및 페이징 처리
        Page<Order> orders = orderService.getOrdersWithSearchAndPaging(keyword, sortedPageable);

        // 페이지네이션 범위 계산
        int currentPage = orders.getNumber() + 1; // 0-based → 1-based
        int totalPages = orders.getTotalPages();
        int startPage = Math.max(1, (currentPage - 1) / 10 * 10 + 1);
        int endPage = Math.min(startPage + 9, totalPages);

        // 모델에 데이터 추가
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

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
