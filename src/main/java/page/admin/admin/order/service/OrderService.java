package page.admin.admin.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.dto.OrderDetailDTO;
import page.admin.admin.order.domain.dto.OrderSummaryChartDTO;
import page.admin.admin.order.domain.dto.OrderSummaryDTO;

import java.util.Date;
import java.util.List;

public interface OrderService {

    /**
     * 전체 주문 조회
     * @return 모든 주문 리스트
     */
    List<Order> getAllOrders();

    /**
     * 주문 번호로 단일 주문 조회
     * @param orderNo 주문 번호
     * @return 주문 정보
     */
    Order getOrderById(Long orderNo);

    /**
     * 특정 주문의 상세 정보 조회
     * @param orderNo 주문 번호
     * @return 주문 상세 정보 DTO 리스트
     */
    List<OrderDetailDTO> getOrderDetails(Long orderNo);

    /**
     * 주문 상태 업데이트
     * @param orderNo 주문 번호
     * @param status 변경할 상태
     */
    void updateOrderStatus(Long orderNo, String status);

    /**
     * 검색 및 페이징 처리된 주문 데이터 조회
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 주문 페이지
     */
    Page<Order> getOrdersWithSearchAndPaging(String keyword, Pageable pageable);
    /**
     * 검색 및 필터링된 모든 주문 데이터 조회 (페이징 없이)
     * @param keyword 검색어
     * @param startDate 시작 날짜 (optional)
     * @param endDate 종료 날짜 (optional)
     * @return 필터링된 주문 리스트
     */
    List<Order> getAllOrdersWithFilters(String keyword, Date startDate, Date endDate);

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
    Page<Order> getOrdersWithFilters(String keyword, Date startDate, Date endDate, Pageable pageable, String sortField, String sortDirection);

    /**
     * 제품별 주문 현황 조회
     * @param pageable 페이징 정보
     * @param keyword 검색어
     * @return 주문 요약 DTO 페이지
     */
    Page<OrderSummaryDTO> getOrderSummariesWithPaging(Pageable pageable, String keyword);

    /**
     * 제품별 주문 현황 차트 데이터 생성
     * @param summaries 주문 요약 리스트
     * @return 차트 데이터
     */
    List<OrderSummaryChartDTO> getOrderSummaryChartData(List<OrderSummaryDTO> summaries);

    /**
     * 제품별 주문 현황 차트 데이터 생성 (QueryDSL 기반)
     * @param keyword 검색어
     * @return 차트 데이터
     */
    List<OrderSummaryChartDTO> getOrderSummaryChartDataWithQuery(String keyword);

    Page<Order> getOrdersByUserIdWithFilters(String userId, String keyword, Pageable pageable, String sortField, String sortDirection);
}
