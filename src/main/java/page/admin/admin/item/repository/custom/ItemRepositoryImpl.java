package page.admin.admin.item.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.QItem;

import java.util.List;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        QItem qItem = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(qItem.itemName.containsIgnoreCase(keyword));
        }

        // 1) 기본 쿼리: 검색 조건 + 페이징
        List<Item> content = queryFactory
                .selectFrom(qItem)
                .where(builder)
                // 2) 동적 정렬 적용 (아래 메서드 활용)
                .orderBy(getOrderSpecifiers(qItem, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        long total = queryFactory
                .selectFrom(qItem)
                .where(builder)
                .fetchCount();

        // 결과 리스트 + 페이징 정보
        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    /**
     * pageable.getSort()를 이용해
     * Querydsl OrderSpecifier 배열을 생성하는 메서드
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(QItem qItem, Pageable pageable) {
        // 만약 정렬 지정이 없다면, 기본 itemId ASC 등으로 처리할 수도 있음
        if (!pageable.getSort().isSorted()) {
            // 여기서 원하면, 기본 정렬을 정할 수도 있음 (예: return new OrderSpecifier[]{qItem.itemId.asc()};
            return new OrderSpecifier[0];
        }

        return pageable.getSort().stream()
                .map(order -> {
                    // order.getProperty() → 예: "itemId", "quantity" 등
                    // order.isAscending() → true / false
                    switch (order.getProperty()) {
                        // 예: "itemId"를 QItem의 필드와 매핑
                        case "itemId":
                            return order.isAscending() ? qItem.itemId.asc() : qItem.itemId.desc();
                        case "salePrice":
                            return order.isAscending() ? qItem.salePrice.asc() : qItem.salePrice.desc();
                        case "quantity":
                            return order.isAscending() ? qItem.quantity.asc() : qItem.quantity.desc();
                        case "purchasePrice":
                            return order.isAscending() ? qItem.purchasePrice.asc() : qItem.purchasePrice.desc();
                        case "itemName":
                            return order.isAscending() ? qItem.itemName.asc() : qItem.itemName.desc();
                        default:
                            // 여기에 없는 필드는 기본으로 itemId 정렬 등으로 처리할 수도 있음
                            return order.isAscending() ? qItem.itemId.asc() : qItem.itemId.desc();
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }
}