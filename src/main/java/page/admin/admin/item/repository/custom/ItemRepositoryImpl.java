package page.admin.admin.item.repository.custom;

import com.querydsl.core.BooleanBuilder;
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
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(item.itemName.containsIgnoreCase(keyword));
        }

        List<Item> content = queryFactory.selectFrom(item)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.itemName.asc()) // 원하는 정렬 기준으로 변경 가능
                .fetch();

        // Count 쿼리
        long total = queryFactory.selectFrom(item)
                .where(builder)
                .fetchCount();

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
