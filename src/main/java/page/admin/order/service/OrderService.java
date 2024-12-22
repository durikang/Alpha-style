package page.admin.order.service;


import org.springframework.data.domain.Page;
import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetailDTO;
import page.admin.order.domain.dto.OrderSummaryDTO;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long orderNo);
    List<OrderDetailDTO> getOrderDetails(Long orderNo);
    void updateOrderStatus(Long orderNo, String status);
    Page<Order> getOrdersWithSearchAndPaging(String keyword, int page, int size, String sortBy, String sortDir);
    Page<OrderSummaryDTO> getOrderSummariesWithPaging(int page, int size, String sortBy, String sortDir);

}