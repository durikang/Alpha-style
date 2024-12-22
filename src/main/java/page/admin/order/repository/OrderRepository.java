package page.admin.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.order.domain.dto.OrderSummaryDTO;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.orderNo = :orderNo")
    Optional<Order> findByIdWithDetails(@Param("orderNo") Long orderNo);

    @Query("SELECT new page.admin.order.domain.dto.OrderSummaryDTO(" +
            "d.item.itemId, " +
            "d.item.itemName, " +
            "SUM(d.quantity), " +
            "SUM(d.subtotal), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송중' THEN d.quantity ELSE 0 END), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송완료' THEN d.quantity ELSE 0 END)) " +
            "FROM Order o " +
            "JOIN o.orderDetails d " +
            "GROUP BY d.item.itemId, d.item.itemName")

    Page<Order> findByUserUsernameContainingOrDeliveryStatusContaining(String username, String deliveryStatus, Pageable pageable);

    /**
     * 제품별 주문 현황 리스트 쿼리
     * @param pageable
     * @return
     */
    @Query("SELECT new page.admin.order.domain.dto.OrderSummaryDTO(" +
            "d.item.itemId, " +
            "d.item.itemName, " +
            "SUM(d.quantity), " +
            "SUM(d.subtotal), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송중' THEN d.quantity ELSE 0 END), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송완료' THEN d.quantity ELSE 0 END)) " +
            "FROM Order o " +
            "JOIN o.orderDetails d " + // OrderDetail과 JOIN
            "JOIN d.item i " +         // Item과 JOIN
            "GROUP BY d.item.itemId, d.item.itemName")
    Page<OrderSummaryDTO> findOrderSummariesWithPaging(Pageable pageable);






}
