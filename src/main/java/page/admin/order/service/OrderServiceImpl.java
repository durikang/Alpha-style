package page.admin.order.service;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetail;
import page.admin.order.domain.dto.OrderDetailDTO;
import page.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.order.repository.OrderRepository;
import page.admin.item.repository.DeliveryCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryCodeRepository deliveryCodeRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderNo) {
        return orderRepository.findByIdWithDetails(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 주문 번호: " + orderNo));
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Long orderNo) {
        Order order = getOrderById(orderNo);
        return order.getOrderDetails().stream()
                .map(detail -> convertToDTO(order, detail))
                .collect(Collectors.toList());
    }

    private OrderDetailDTO convertToDTO(Order order, OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setItemId(detail.getItem() != null ? detail.getItem().getItemId() : null);
        dto.setItemName(detail.getItem() != null ? detail.getItem().getItemName() : "상품 없음");
        dto.setItemPrice(detail.getItem() != null ? detail.getItem().getPrice() : 0L);
        dto.setQuantity(detail.getQuantity());
        dto.setSubtotal(detail.getSubtotal() != null ? detail.getSubtotal() : 0.0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dto.setOrderDate(order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).format(formatter));
        dto.setDelivaryStatus(order.getDeliveryStatus());

        return dto;
    }

    @Override
    public void updateOrderStatus(Long orderNo, String status) {
        Order order = getOrderById(orderNo);
        order.setDeliveryStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersWithSearchAndPaging(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword.isBlank()) {
            return orderRepository.findAll(pageable);
        } else {
            return orderRepository.findByUserUsernameContainingOrDeliveryStatusContaining(keyword, keyword, pageable);
        }
    }

    @Override
    public Page<OrderSummaryDTO> getOrderSummariesWithPaging(int page, int size, String sortBy, String sortDir) {
        Sort sort;

        // sortBy 필드를 실제 HQL 별칭에 맞게 매핑
        if (sortBy.equals("itemId")) {
            sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by("d.item.itemId").ascending()
                    : Sort.by("d.item.itemId").descending();
        } else if (sortBy.equals("totalQuantity")) {
            sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by("SUM(d.quantity)").ascending()
                    : Sort.by("SUM(d.quantity)").descending();
        } else {
            sort = Sort.unsorted(); // 기본 정렬
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.findOrderSummariesWithPaging(pageable);
    }




}
