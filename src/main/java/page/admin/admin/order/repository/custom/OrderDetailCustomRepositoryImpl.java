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

        // 1) 기간 조건 (startDate와 endDate는 이미 LocalDateTime이므로 직접 사용)
        if (searchRequest.getStartDate() != null) {
            condition.and(order.orderDate.goe(searchRequest.getStartDate()));
        }
        if (searchRequest.getEndDate() != null) {
            condition.and(order.orderDate.loe(searchRequest.getEndDate()));
        }

        // 2) 상품명 조건
        if (searchRequest.getItemName() != null && !searchRequest.getItemName().isEmpty()) {
            condition.and(item.itemName.containsIgnoreCase(searchRequest.getItemName()));
        }

        // 3) 판매자 아이디 조건
        if (searchRequest.getSellerId() != null && !searchRequest.getSellerId().isEmpty()) {
            condition.and(seller.userId.eq(searchRequest.getSellerId()));
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
