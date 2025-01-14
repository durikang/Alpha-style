package page.admin.admin.order.service;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.dto.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderService {

    /**
     * 주문 번호로 단일 주문 조회
     * @param orderNo 주문 번호
     * @return 주문 정보
     */
    Order getOrderById(Long orderNo);

    /**
     * 주문 상태 업데이트
     * @param orderNo 주문 번호
     * @param status 변경할 상태
     */
    void updateOrderStatus(Long orderNo, String status);

    /**
     * 검색 및 필터링된 주문 데이터 조회 (페이징 및 정렬 포함)
     * @param keyword 검색어
     * @param startDate 시작 날짜 (optional)
     * @param endDate 종료 날짜 (optional)
     * @param pageable 페이징 정보
     * @param sortField 정렬 필드
     * @param sortDirection 정렬 방향
     * @return 필터링된 주문 페이지
     */
    Page<Order> getOrdersWithFilters(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sortField, String sortDirection);

    /**
     * 제품별 주문 현황 조회
     * @param pageable 페이징 정보
     * @param keyword 검색어
     * @return 주문 요약 DTO 페이지
     */
    Page<OrderSummaryDTO> getOrderSummariesWithPaging(Pageable pageable, String keyword);

    Page<Order> getOrdersByUserIdWithFilters(String userId, String keyword, Pageable pageable, String sortField, String sortDirection);

    /**
     * 주문 생성 시 판매량 업데이트를 포함한 주문 생성
     * @param order 생성할 주문 객체
     * @return 생성된 주문 객체
     */
    Order createOrder(Order order);

    /**
     * 상품별 주문 상세 목록
     */
    Page<ItemOrderDetailDTO> getItemOrderDetails(Long itemId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);

    // 추가 메서드: (itemId, 기간)에 대한 판매수량 집계
    List<Tuple> analyzeItemSales(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    Page<CustomerPurchaseSummaryDTO> getBuyerOrderSummaries(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);



    Page<CustomerPurchaseSummaryDTO> getBuyerOrderSummaries(
            String keyword,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable,
            String sortField,
            String sortDirection);

    /**
     * 매출 요약 데이터 분석
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일별 매출 요약 튜플
     */
    List<Tuple> analyzeSalesSummary(LocalDateTime startDate, LocalDateTime endDate);

    List<Tuple> analyzeCustomerPurchases(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
