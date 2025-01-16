package page.admin.admin.order.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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
import page.admin.admin.order.repository.OrderDetailRepository;
import page.admin.admin.order.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final JPAQueryFactory queryFactory;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository; // ItemRepository 주입
    private final OrderDetailRepository orderDetailRepository;

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
    public Page<Order> getOrdersWithFilters(
            String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sortField, String sortDirection) {
        QOrder order = QOrder.order;
        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건 추가
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(order.user.username.containsIgnoreCase(keyword)
                    .or(order.user.userId.containsIgnoreCase(keyword)));
        }
        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // 정렬 필드와 방향
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortField, sortDirection);

        List<Order> results = queryFactory
                .selectFrom(order)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(order).where(builder).fetchCount();
        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public Page<CustomerPurchaseSummaryDTO> getBuyerOrderSummaries(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QOrder order = QOrder.order;
        QMember user = QMember.member;
        QOrderDetail detail = QOrderDetail.orderDetail;

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 검색
        if (keyword != null && !keyword.isBlank()) {
            builder.and(user.username.containsIgnoreCase(keyword)
                    .or(user.userId.containsIgnoreCase(keyword)));
        }

        // 날짜 필터링
        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // QueryDSL로 고객별 구매 요약 데이터 조회 (매출만 포함)
        var query = queryFactory
                .select(Projections.constructor(
                        CustomerPurchaseSummaryDTO.class,
                        user.userId,
                        user.username,
                        detail.countDistinct().as("purchaseCount"), // 구매 횟수
                        detail.subtotal.sum().add(detail.vat.sum()).as("totalAmount") // 총 구매 금액
                ))
                .from(order)
                .join(order.user, user)
                .join(order.orderDetails, detail)
                .where(builder.and(detail.transactionType.eq(1))) // 매출(1)만 필터링
                .groupBy(user.userId, user.username)
                .orderBy(detail.countDistinct().desc()) // 구매 횟수 기준 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<CustomerPurchaseSummaryDTO> results = query.fetch();
        long total = queryFactory
                .select(detail.countDistinct())
                .from(order)
                .join(order.user, user)
                .join(order.orderDetails, detail)
                .where(builder.and(detail.transactionType.eq(1)))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<CustomerPurchaseSummaryDTO> getBuyerOrderSummaries(
            String keyword,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable,
            String sortField,
            String sortDirection) {

        QOrder order = QOrder.order;
        QMember user = QMember.member;
        QOrderDetail detail = QOrderDetail.orderDetail;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(user.username.containsIgnoreCase(keyword)
                    .or(user.userId.containsIgnoreCase(keyword)));
        }

        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // 집계 컬럼 정의
        var totalAmount = detail.subtotal.sum().add(detail.vat.sum());
        var purchaseCount = detail.countDistinct();

        // 동적 정렬 기준 생성
        OrderSpecifier<?> orderSpecifier;
        if ("totalAmount".equals(sortField)) {
            orderSpecifier = sortDirection.equalsIgnoreCase("asc") ? totalAmount.asc() : totalAmount.desc();
        } else if ("purchaseCount".equals(sortField)) {
            orderSpecifier = sortDirection.equalsIgnoreCase("asc") ? purchaseCount.asc() : purchaseCount.desc();
        } else {
            orderSpecifier = order.orderDate.desc(); // 기본 정렬
        }

        var query = queryFactory
                .select(Projections.constructor(
                        CustomerPurchaseSummaryDTO.class,
                        user.userId,
                        user.username,
                        purchaseCount.as("purchaseCount"), // 구매 횟수
                        totalAmount.as("totalAmount") // 총 금액
                ))
                .from(order)
                .join(order.user, user)
                .join(order.orderDetails, detail)
                .where(builder.and(detail.transactionType.eq(1))) // 매출만 포함
                .groupBy(user.userId, user.username) // 그룹화
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<CustomerPurchaseSummaryDTO> results = query.fetch();
        long total = queryFactory
                .select(detail.countDistinct())
                .from(order)
                .join(order.user, user)
                .join(order.orderDetails, detail)
                .where(builder.and(detail.transactionType.eq(1)))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
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

    @Override
    public Page<ItemOrderDetailDTO> getItemOrderDetails(Long itemId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        QOrderDetail od = QOrderDetail.orderDetail;
        QOrder o = QOrder.order;
        QItem i = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(i.itemId.eq(itemId));
        if (startDate != null) {
            builder.and(o.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(o.orderDate.loe(endDate));
        }

        var query = queryFactory
                .select(Projections.constructor(ItemOrderDetailDTO.class,
                        o.orderNo,
                        o.orderDate,
                        i.itemId,
                        i.itemName,
                        od.quantity,
                        od.subtotal,
                        od.vat,
                        o.deliveryStatus
                ))
                .from(od)
                .join(od.order, o)
                .join(od.item, i)
                .where(builder)
                .orderBy(o.orderDate.desc());

        long total = query.fetchCount();
        List<ItemOrderDetailDTO> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public List<Tuple> analyzeItemSales(Long itemId, LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 조건 필터링
        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        return queryFactory
                .select(
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')", order.orderDate // 일별로 그룹화
                        ).as("formattedDate"),
                        member.userId.as("userId"), // 회원 ID
                        member.username.as("username"), // 회원 이름
                        detail.subtotal.sum().add(detail.vat.sum()).as("totalSales"), // 총 매출 금액
                        detail.subtotal.avg().add(detail.vat.avg()).as("averageSales") // 평균 매출 금액
                )
                .from(order)
                .join(order.orderDetails, detail) // 주문-세부사항 조인
                .join(order.user, member) // 주문-회원 조인
                .where(builder) // 필터 적용
                .groupBy(order.orderDate, member.userId, member.username) // 날짜와 회원별 그룹화
                .orderBy(order.orderDate.asc()) // 날짜순 정렬
                .fetch();
    }



    @Override
    public Map<String, Object> analyzeCategorySales(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.orderDate.goe(startDate));
        builder.and(order.orderDate.loe(endDate));
        builder.and(detail.transactionType.eq(1)); // 매출만 포함

        // 큰 카테고리별 매출
        List<Tuple> mainCategoryResults = queryFactory
                .select(
                        item.mainCategory.mainCategoryName,
                        detail.subtotal.sum().add(detail.vat.sum())
                )
                .from(order)
                .join(order.orderDetails, detail)
                .join(detail.item, item)
                .where(builder)
                .groupBy(item.mainCategory.mainCategoryName)
                .fetch();

        // 서브 카테고리별 매출
        List<Tuple> subCategoryResults = queryFactory
                .select(
                        item.subCategory.subCategoryName,
                        detail.subtotal.sum().add(detail.vat.sum())
                )
                .from(order)
                .join(order.orderDetails, detail)
                .join(detail.item, item)
                .where(builder)
                .groupBy(item.subCategory.subCategoryName)
                .fetch();

        Map<String, Object> response = new HashMap<>();
        response.put("mainCategories", mainCategoryResults.stream()
                .map(result -> Map.of(
                        "name", result.get(0, String.class),
                        "value", result.get(1, Long.class)
                ))
                .toList());
        response.put("subCategories", subCategoryResults.stream()
                .map(result -> Map.of(
                        "name", result.get(0, String.class),
                        "value", result.get(1, Long.class)
                ))
                .toList());

        response.put("title", "카테고리별 매출 분석");
        return response;
    }



    @Override
    public List<Tuple> analyzeSalesSummary(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;

        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 필터 조건
        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // 매출만 포함 (transactionType = 1)
        builder.and(detail.transactionType.eq(1));

        // 쿼리 실행
        return queryFactory
                .select(
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')", order.orderDate // 날짜 포맷
                        ).as("formattedDate"),
                        detail.subtotal.sum().add(detail.vat.sum()).as("totalSales"), // 총 매출
                        detail.subtotal.avg().add(detail.vat.avg()).as("averageSales") // 평균 매출
                )
                .from(order)
                .join(order.orderDetails, detail)
                .where(builder)
                .groupBy(order.orderDate)
                .orderBy(order.orderDate.asc())
                .fetch();
    }



    @Override
    public List<Tuple> analyzeCustomerPurchases(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        QOrderDetail detail = QOrderDetail.orderDetail;
        QItem item = QItem.item; // 상품 정보
        QMember member = QMember.member; // 회원 정보

        BooleanBuilder builder = new BooleanBuilder();

        // 회원 필터
        builder.and(order.user.userNo.eq(customerId));

        // 날짜 필터
        if (startDate != null) {
            builder.and(order.orderDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(order.orderDate.loe(endDate));
        }

        // 쿼리 실행
        return queryFactory
                .select(
                        Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", order.orderDate).as("formattedDate"), // 날짜 (String)
                        detail.quantity.sum().castToNum(Long.class).as("totalQuantity"), // 총 구매 수량 (Long)
                        detail.subtotal.sum().add(detail.vat.sum()).castToNum(Long.class).as("totalAmount"), // 총 구매 금액 (Long)
                        item.mainCategory.mainCategoryName.as("mainCategory"), // 상품 대분류 (String)
                        item.subCategory.subCategoryName.as("subCategory"), // 상품 소분류 (String)
                        detail.subtotal.avg().add(detail.vat.avg()).castToNum(Double.class).as("averageAmount"), // 평균 구매 금액 (Double)
                        member.username.as("username") // 회원 이름 (String)
                )
                .from(order)
                .join(order.orderDetails, detail)
                .join(detail.item, item) // 아이템 조인
                .join(order.user, member) // 회원 조인
                .where(builder)
                .groupBy(order.orderDate, item.mainCategory.mainCategoryName, item.subCategory.subCategoryName, member.username) // 그룹화
                .orderBy(order.orderDate.asc())
                .fetch();
    }






}
