package page.admin.admin.order.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.SalesSearchRequest;
import page.admin.admin.order.domain.QOrder;
import page.admin.admin.order.domain.QOrderDetail;
import page.admin.admin.item.domain.QItem;
import page.admin.admin.member.domain.QMember;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderDetailCustomRepositoryImpl implements OrderDetailCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderDetail> searchSalesRecords(SalesSearchRequest searchRequest, Pageable pageable) {
        QOrderDetail orderDetail = QOrderDetail.orderDetail;
        QOrder order = QOrder.order;
        QItem item = QItem.item;
        QMember seller = QMember.member;

        // 검색 조건을 Querydsl의 BooleanBuilder로 구성
        BooleanBuilder condition = new BooleanBuilder();

        // ...
        // 1) 기간 조건 (startDate, endDate는 LocalDate)
        if (searchRequest.getStartDate() != null) {
            // "00:00:00" 시점으로 변환
            condition.and(order.orderDate.goe(searchRequest.getStartDate().atStartOfDay()));
        }
        if (searchRequest.getEndDate() != null) {
            // "23:59:59" 시점으로 변환
            condition.and(order.orderDate.loe(searchRequest.getEndDate().atTime(23, 59, 59)));
        }


        // 2) keyword(= 상품명 or 판매자ID)
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isBlank()) {
            String kw = searchRequest.getKeyword();
            condition.and(
                    item.itemName.containsIgnoreCase(kw)
                            .or(seller.userId.containsIgnoreCase(kw))
            );
        }

        // 4) 배송 상태
        if (searchRequest.getDeliveryStatus() != null && !searchRequest.getDeliveryStatus().isEmpty()) {
            condition.and(order.deliveryStatus.eq(searchRequest.getDeliveryStatus()));
        }

        // 메인 쿼리
        List<OrderDetail> content = queryFactory
                .selectFrom(orderDetail)
                .join(orderDetail.order, order).fetchJoin()
                .join(orderDetail.item, item).fetchJoin()
                .join(item.seller, seller).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.orderDate.desc()) // 최근 주문일 순으로
                .fetch();

        // 전체 개수 조회(페이징을 위해)
        long total = queryFactory
                .select(orderDetail.count())
                .from(orderDetail)
                .join(orderDetail.order, order)
                .join(orderDetail.item, item)
                .join(item.seller, seller)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
