package page.admin.admin.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.admin.sales.domain.dto.SalesRecordDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문 상세 조회
     * @param orderNo 주문 번호
     * @return 주문 정보 및 상세 정보
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderNo = :orderNo")
    Optional<Order> findByIdWithDetails(@Param("orderNo") Long orderNo);

    /**
     * 검색 및 페이징 처리된 주문 데이터 조회
     * @param username 사용자 이름
     * @param deliveryStatus 배송 상태
     * @param pageable 페이징 정보
     * @return 주문 페이지
     */
    Page<Order> findByUserUsernameContainingOrDeliveryStatusContaining(String username, String deliveryStatus, Pageable pageable);

    @Query("SELECT new page.admin.admin.order.domain.dto.OrderSummaryDTO(" +
            "d.item.itemId, " +
            "d.item.itemName, " +
            "d.item.mainCategory.mainCategoryName, " +
            "SUM(d.quantity), " +
            "SUM(d.subtotal * 1.0), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송중' THEN d.quantity ELSE 0 END), " +
            "SUM(CASE WHEN o.deliveryStatus = '배송완료' THEN d.quantity ELSE 0 END), " +
            "d.item.seller.username, " +
            "CAST(d.item.createdDate AS LocalDateTime)) " + // 명시적 타입 캐스팅
            "FROM Order o " +
            "JOIN o.orderDetails d " +
            "GROUP BY d.item.itemId, d.item.itemName, d.item.mainCategory.mainCategoryName, " +
            "d.item.seller.username, d.item.createdDate " +
            "ORDER BY SUM(d.quantity) DESC")
    Page<OrderSummaryDTO> findOrderSummariesWithPaging(Pageable pageable);

    /**
     * 판매 기록 페이징 및 검색 조회
     */
    @Query("SELECT new page.admin.admin.sales.domain.dto.SalesRecordDto(" +
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
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}
