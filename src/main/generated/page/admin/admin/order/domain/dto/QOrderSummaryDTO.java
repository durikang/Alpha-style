package page.admin.admin.order.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * page.admin.admin.order.domain.dto.QOrderSummaryDTO is a Querydsl Projection type for OrderSummaryDTO
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderSummaryDTO extends ConstructorExpression<OrderSummaryDTO> {

    private static final long serialVersionUID = 276804043L;

    public QOrderSummaryDTO(com.querydsl.core.types.Expression<Long> itemId, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<Long> totalQuantity, com.querydsl.core.types.Expression<Double> totalAmount, com.querydsl.core.types.Expression<Long> shippingQuantity, com.querydsl.core.types.Expression<Long> completedQuantity) {
        super(OrderSummaryDTO.class, new Class<?>[]{long.class, String.class, long.class, double.class, long.class, long.class}, itemId, itemName, totalQuantity, totalAmount, shippingQuantity, completedQuantity);
    }

    public QOrderSummaryDTO(com.querydsl.core.types.Expression<Long> itemId, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<String> categoryName, com.querydsl.core.types.Expression<Long> totalQuantity, com.querydsl.core.types.Expression<Double> totalAmount, com.querydsl.core.types.Expression<Long> shippingQuantity, com.querydsl.core.types.Expression<Long> completedQuantity, com.querydsl.core.types.Expression<String> sellerName, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdDate) {
        super(OrderSummaryDTO.class, new Class<?>[]{long.class, String.class, String.class, long.class, double.class, long.class, long.class, String.class, java.time.LocalDateTime.class}, itemId, itemName, categoryName, totalQuantity, totalAmount, shippingQuantity, completedQuantity, sellerName, createdDate);
    }

}

