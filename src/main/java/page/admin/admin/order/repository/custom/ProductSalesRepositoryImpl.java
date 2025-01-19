package page.admin.admin.order.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections; // Projections.*
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import page.admin.admin.item.domain.QItem;
import page.admin.admin.member.domain.QMember;
import page.admin.admin.order.domain.QOrder;
import page.admin.admin.order.domain.QOrderDetail;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.domain.dto.SalesSearchRequest;

import java.util.List;

public class ProductSalesRepositoryImpl implements ProductSalesRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductSalesDTO> findProductSales(SalesSearchRequest searchRequest, Pageable pageable) {
        QOrderDetail od = QOrderDetail.orderDetail;
        QOrder o = QOrder.order;
        QItem i = QItem.item;
        QMember m = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 범위 필터링
        if (searchRequest.getStartDateTime() != null) {
            builder.and(o.orderDate.goe(searchRequest.getStartDateTime()));
        }
        if (searchRequest.getEndDateTime() != null) {
            builder.and(o.orderDate.loe(searchRequest.getEndDateTime()));
        }

        // 검색어 필터링
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isBlank()) {
            String kw = searchRequest.getKeyword();
            builder.and(
                    i.itemName.containsIgnoreCase(kw)
                            .or(m.userId.containsIgnoreCase(kw))
            );
        }

        // 배송 상태 필터링
        if (searchRequest.getDeliveryStatus() != null && !searchRequest.getDeliveryStatus().isEmpty()) {
            builder.and(o.deliveryStatus.eq(searchRequest.getDeliveryStatus()));
        }

        // 매출 데이터 필터링
        builder.and(od.transactionType.eq(1));

        // 메인 쿼리
        JPAQuery<ProductSalesDTO> query = queryFactory
                .select(Projections.constructor(
                        ProductSalesDTO.class,
                        o.orderNo, o.orderDate, i.itemName, m.userId,
                        od.quantity, i.salePrice, od.vat,
                        od.subtotal.add(od.vat.longValue()).doubleValue(),
                        o.deliveryStatus))
                .from(od)
                .join(od.order, o)
                .join(od.item, i)
                .join(i.seller, m)
                .where(builder);

        // 정렬 처리
        pageable.getSort().forEach(order -> {
            switch (order.getProperty()) {
                case "orderNo" -> query.orderBy(order.isAscending() ? o.orderNo.asc() : o.orderNo.desc());
                case "orderDate" -> query.orderBy(order.isAscending() ? o.orderDate.asc() : o.orderDate.desc());
                case "itemName" -> query.orderBy(order.isAscending() ? i.itemName.asc() : i.itemName.desc());
                case "quantity" -> query.orderBy(order.isAscending() ? od.quantity.asc() : od.quantity.desc());
                case "salePrice" -> query.orderBy(order.isAscending() ? i.salePrice.asc() : i.salePrice.desc());
                case "totalAmount" -> query.orderBy(order.isAscending() ? od.subtotal.add(od.vat).asc() : od.subtotal.add(od.vat).desc());
                default -> query.orderBy(o.orderDate.desc());
            }
        });

        // 페이징 처리
        List<ProductSalesDTO> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 레코드 수 계산
        long total = queryFactory
                .select(od.count())
                .from(od)
                .join(od.order, o)
                .join(od.item, i)
                .join(i.seller, m)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }


}

