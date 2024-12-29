package page.admin.admin.order.service;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.QItem;
import page.admin.admin.member.domain.QMember;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.QOrder;
import page.admin.admin.order.domain.QOrderDetail;
import page.admin.admin.order.domain.dto.*;
import page.admin.admin.order.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final JPAQueryFactory queryFactory;
    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        QOrder order = QOrder.order;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;

        Order result = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .where(order.orderNo.eq(id))
                .fetchOne();

        if (result == null) {
            throw new IllegalArgumentException("주문 번호에 해당하는 정보를 찾을 수 없습니다.");
        }
        return result;
    }


    @Override
    public List<OrderDetailDTO> getOrderDetails(Long orderNo) {
        Order order = getOrderById(orderNo);
        return order.getOrderDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDetailDTO convertToDTO(OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderNo(detail.getOrder().getOrderNo());
        dto.setItemId(detail.getItem() != null ? detail.getItem().getItemId() : null);
        dto.setItemName(detail.getItem() != null ? detail.getItem().getItemName() : "상품 없음");
        dto.setQuantity(detail.getQuantity() != null ? detail.getQuantity() : 0);
        dto.setSubtotal(detail.getSubtotal() != null ? detail.getSubtotal() : 0L);
        dto.setDeliveryStatus(detail.getOrder().getDeliveryStatus());
        return dto;
    }

    @Override
    public void updateOrderStatus(Long orderNo, String status) {
        Order order = getOrderById(orderNo);
        order.setDeliveryStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersWithSearchAndPaging(String keyword, Pageable pageable) {
        QOrder order = QOrder.order;
        QMember user = QMember.member;

        // QueryDSL 쿼리 작성
        var query = queryFactory
                .selectFrom(order)
                .join(order.user, user)
                .fetchJoin()
                .where(
                        user.username.containsIgnoreCase(keyword)
                                .or(user.userId.containsIgnoreCase(keyword))
                )
                .orderBy(order.orderDate.desc());

        // 페이징 처리
        List<Order> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<OrderSummaryDTO> getOrderSummariesWithPaging(Pageable pageable, String keyword) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;
        QItem item = QItem.item;

        var query = queryFactory
                .select(new QOrderSummaryDTO(
                        item.itemId,
                        item.itemName,
                        item.mainCategory.mainCategoryName,
                        detail.quantity.sum().castToNum(Long.class),
                        detail.subtotal.sum().castToNum(Double.class),
                        Expressions.asNumber(
                                order.deliveryStatus
                                        .when("배송중").then(detail.quantity)
                                        .otherwise(0)
                                        .sum()
                        ).castToNum(Long.class),
                        Expressions.asNumber(
                                order.deliveryStatus
                                        .when("배송완료").then(detail.quantity)
                                        .otherwise(0)
                                        .sum()
                        ).castToNum(Long.class),
                        item.seller.username,
                        item.createdDate
                ))
                .from(order)
                .join(order.orderDetails, detail)
                .join(detail.item, item)
                .where(item.itemName.containsIgnoreCase(keyword)) // 검색 조건
                .groupBy(item.itemId, item.itemName, item.mainCategory.mainCategoryName,
                        item.seller.username, item.createdDate)
                .orderBy(detail.quantity.sum().desc());

        // 쿼리 디버깅
        System.out.println("Generated Query: " + query);

        List<OrderSummaryDTO> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (results.isEmpty()) {
            System.out.println("Query 결과가 비어 있습니다.");
        }

        long total = query.fetchCount();
        return new PageImpl<>(results, pageable, total);
    }



    @Override
    public List<OrderSummaryChartDTO> getOrderSummaryChartData(List<OrderSummaryDTO> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            System.out.println("Summary 데이터가 비어 있습니다.");
            return new ArrayList<>();
        }

        return summaries.stream()
                .map(summary -> new OrderSummaryChartDTO(
                        summary.getItemName(),
                        summary.getTotalQuantity(),
                        summary.getTotalAmount()
                ))
                .toList();
    }


    @Override
    public List<OrderSummaryChartDTO> getOrderSummaryChartDataWithQuery(String keyword) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;
        QItem item = QItem.item;

        // QueryDSL 쿼리 작성
        var query = queryFactory
                .select(new QOrderSummaryChartDTO(
                        item.itemName,
                        detail.quantity.sum().castToNum(Long.class),  // 총 주문 수량
                        detail.subtotal.sum().castToNum(Double.class) // 총 주문 금액
                ))
                .from(order)
                .join(order.orderDetails, detail)
                .join(detail.item, item)
                .where(item.itemName.containsIgnoreCase(keyword)) // 검색 조건
                .groupBy(item.itemName) // 그룹화
                .orderBy(detail.quantity.sum().desc()); // 총 주문 수량 기준 정렬

        System.out.println("Generated Chart Query: " + query);

        // 데이터베이스에서 결과 가져오기
        return query.fetch();
    }

}
