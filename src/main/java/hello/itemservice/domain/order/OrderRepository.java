package hello.itemservice.domain.order;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {

    private static final Map<Long, Order> orderStore = new HashMap<>(); // 주문 저장소
    private static final Map<Long, List<OrderDetail>> orderDetailStore = new HashMap<>(); // 주문 상세 저장소
    private static final AtomicLong orderSequence = new AtomicLong(0L); // 주문 ID 자동 생성
    private static final AtomicLong orderDetailSequence = new AtomicLong(0L); // 주문 상세 ID 자동 생성

    // 주문 저장
    public Order saveOrder(Order order) {
        order.setOrderNo(orderSequence.incrementAndGet());
        orderStore.put(order.getOrderNo(), order);

        // 주문 생성 시 주문 상세를 자동으로 저장
        if (order.getDetails() != null) {
            for (OrderDetail detail : order.getDetails()) {
                saveOrderDetail(order.getOrderNo(), detail);
            }
        }
        return order;
    }

    // 주문 상세 저장
    public OrderDetail saveOrderDetail(Long orderNo, OrderDetail orderDetail) {
        orderDetail.setOrderDetailNo(orderDetailSequence.incrementAndGet());
        orderDetail.setOrderNo(orderNo);

        List<OrderDetail> details = orderDetailStore.computeIfAbsent(orderNo, k -> new ArrayList<>());
        details.add(orderDetail);
        return orderDetail;
    }

    // 주문 상세 조회 (주문 번호로)
    public List<OrderDetail> findOrderDetailsByOrderNo(Long orderNo) {
        return orderDetailStore.getOrDefault(orderNo, Collections.emptyList());
    }

    // 주문 번호로 특정 주문 조회 (추가된 메서드)
    public Optional<Order> findById(Long orderNo) {
        return Optional.ofNullable(orderStore.get(orderNo));
    }

    // 모든 주문 조회
    public List<Order> findAll() {
        return new ArrayList<>(orderStore.values());
    }

    // 주문 상태 업데이트
    public void updateOrderStatus(Long orderNo, String status) {
        Order order = orderStore.get(orderNo);
        if (order != null) {
            order.setDelivaryStatus(status);
        }
    }

    // 특정 사용자의 모든 주문 조회 (선택 사항)
    public List<Order> findByUserNo(Long userNo) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (Objects.equals(order.getUserNo(), userNo)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }
    // 특정 주문 삭제
    public void deleteOrder(Long orderNo) {
        orderStore.remove(orderNo);
        orderDetailStore.remove(orderNo); // 관련 주문 상세도 삭제
    }

    // 특정 주문 상세 삭제
    public void deleteOrderDetail(Long orderDetailNo) {
        orderDetailStore.values().forEach(details ->
                details.removeIf(detail -> detail.getOrderDetailNo().equals(orderDetailNo))
        );
    }
    // 주문 업데이트
    public void updateOrder(Order updatedOrder) {
        Order existingOrder = orderStore.get(updatedOrder.getOrderNo());
        if (existingOrder != null) {
            existingOrder.setOrderDate(updatedOrder.getOrderDate());
            existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
            existingOrder.setDelivaryStatus(updatedOrder.getDelivaryStatus());
            // 필요한 필드를 업데이트
        }
    }

    // 주문 상세 업데이트
    public void updateOrderDetail(OrderDetail updatedDetail) {
        List<OrderDetail> details = orderDetailStore.get(updatedDetail.getOrderNo());
        if (details != null) {
            for (OrderDetail detail : details) {
                if (detail.getOrderDetailNo().equals(updatedDetail.getOrderDetailNo())) {
                    detail.setQuantity(updatedDetail.getQuantity());
                    detail.setSubtotal(updatedDetail.getSubtotal());
                    // 필요한 필드를 업데이트
                }
            }
        }
    }

}
