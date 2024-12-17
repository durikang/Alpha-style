package page.admin.order.service;


import page.admin.order.domain.Order;
import page.admin.order.domain.OrderDetailDTO;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long orderNo);
    List<OrderDetailDTO> getOrderDetails(Long orderNo);
    void updateOrderStatus(Long orderNo, String status);
    void updateMultipleOrderStatus(List<Long> orderNos, String status);
}