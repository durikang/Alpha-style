package page.admin.admin.order.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.dto.OrderDetailDTO;
import page.admin.admin.order.domain.dto.OrderSummaryDTO;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long orderNo);
    List<OrderDetailDTO> getOrderDetails(Long orderNo);
    void updateOrderStatus(Long orderNo, String status);
    Page<Order> getOrdersWithSearchAndPaging(String keyword, Pageable pageable);
    Page<OrderSummaryDTO> getOrderSummariesWithPaging(int page, int size, String sortBy, String sortDir);

}