package page.admin.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.sales.domain.dto.SalesRecordDto;

import java.time.LocalDate;
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


    /**
     * 판매 기록 조회 (페이징, 검색, 정렬 지원)
     * @param pageable 페이징 및 정렬 정보
     * @param keyword 검색어 (구매자 이름 또는 상품명)
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 판매 기록 페이지
     */
    @Query("SELECT new page.admin.sales.domain.dto.SalesRecordDto(" +
            "o.orderNo, " +
            "o.orderDate, " +
            "m.username, " +
            "i.itemName, " +
            "od.quantity, " +
            "i.salePrice, " +
            "CAST((od.quantity * i.salePrice) AS Long), " +
            "o.deliveryStatus) " +
            "FROM Order o " +
            "JOIN o.user m " +
            "JOIN o.orderDetails od " +
            "JOIN od.item i " +
            "WHERE (:keyword IS NULL OR m.username LIKE %:keyword% OR i.itemName LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR o.orderDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.orderDate <= :endDate)")
    Page<SalesRecordDto> findSalesRecordsWithPaging(Pageable pageable,
                                                    @Param("keyword") String keyword,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}
