package page.admin.order.service;

import org.springframework.context.annotation.Primary;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetail;
import page.admin.order.domain.OrderDetailDTO;
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
        dto.setProductId(detail.getItem() != null ? detail.getItem().getId() : null);
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
    public void updateMultipleOrderStatus(List<Long> orderNos, String status) {
        orderRepository.updateDeliveryStatusBatch(orderNos, status);
    }
}
