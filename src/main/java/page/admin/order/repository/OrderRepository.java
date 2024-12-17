package page.admin.order.repository;

import page.admin.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.orderNo = :orderNo")
    Optional<Order> findByIdWithDetails(@Param("orderNo") Long orderNo);

    // 대량 상태 업데이트 쿼리
    @Query("UPDATE Order o SET o.deliveryStatus = :status WHERE o.orderNo IN :orderNos")
    void updateDeliveryStatusBatch(@Param("orderNos") List<Long> orderNos, @Param("status") String status);
}
