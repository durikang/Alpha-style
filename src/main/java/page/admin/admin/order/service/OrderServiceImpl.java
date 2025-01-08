package page.admin.admin.order.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.QItem;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.admin.member.domain.QMember;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.QOrder;
import page.admin.admin.order.domain.QOrderDetail;
import page.admin.admin.order.domain.dto.*;
import page.admin.admin.order.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final JPAQueryFactory queryFactory;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository; // ItemRepository 주입

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
    public void updateOrderStatus(Long orderNo, String status) {
        Order order = getOrderById(orderNo);
        order.setDeliveryStatus(status);
        orderRepository.save(order);
    }


    @Override
    public Page<Order> getOrdersWithFilters(String keyword, Date startDate, Date endDate, Pageable pageable, String sortField, String sortDirection) {
        QOrder order = QOrder.order;
        QMember user = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(
                    user.username.containsIgnoreCase(keyword)
                            .or(user.userId.containsIgnoreCase(keyword))
            );
        }

        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }

        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // 정렬 필드와 방향을 기반으로 OrderSpecifier 생성
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortField, sortDirection);

        // QueryDSL 쿼리 작성
        var query = queryFactory
                .selectFrom(order)
                .join(order.user, user)
                .fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier);

        if (pageable.isPaged()) {
            // 페이징이 활성화된 경우
            List<Order> results = query
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            long total = query.fetchCount();

            return new PageImpl<>(results, pageable, total);
        } else {
            // 페이징이 비활성화된 경우 (모든 데이터 조회)
            List<Order> results = query.fetch();

            long total = results.size();

            return new PageImpl<>(results, pageable, total);
        }
    }

    /**
     * 정렬 필드와 방향에 따라 OrderSpecifier를 반환합니다.
     * @param sortField 정렬 필드
     * @param sortDirection 정렬 방향 (asc 또는 desc)
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
        switch (sortField) {
            case "orderNo":
                return sortDirection.equalsIgnoreCase("asc") ? QOrder.order.orderNo.asc() : QOrder.order.orderNo.desc();
            case "user.username":
                return sortDirection.equalsIgnoreCase("asc") ? QMember.member.username.asc() : QMember.member.username.desc();
            case "orderDate":
                return sortDirection.equalsIgnoreCase("asc") ? QOrder.order.orderDate.asc() : QOrder.order.orderDate.desc();
            case "totalAmount":
                return sortDirection.equalsIgnoreCase("asc") ? QOrder.order.totalAmount.asc() : QOrder.order.totalAmount.desc();
            case "deliveryStatus":
                return sortDirection.equalsIgnoreCase("asc") ? QOrder.order.deliveryStatus.asc() : QOrder.order.deliveryStatus.desc();
            default:
                return QOrder.order.orderDate.desc(); // 기본 정렬
        }
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
    public Page<Order> getOrdersByUserIdWithFilters(
            String userId, String keyword, Pageable pageable, String sortField, String sortDirection
    ) {
        QOrder order = QOrder.order;
        QMember user = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.userId.eq(userId)); // 로그인한 사용자 ID로 필터링

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(
                    user.username.containsIgnoreCase(keyword)
                            .or(order.deliveryStatus.containsIgnoreCase(keyword))
            );
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortField, sortDirection);

        var query = queryFactory
                .selectFrom(order)
                .join(order.user, user)
                .fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier);

        List<Order> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Order createOrder(Order order) {
        // 주문 상세(OrderDetail) 생성 시 판매량 업데이트
        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getItem() != null && detail.getQuantity() != null) {
                Item item = detail.getItem();
                item.setSalesCount(item.getSalesCount() + detail.getQuantity());
                itemRepository.save(item);
            }
        }

        return orderRepository.save(order);
    }


}
