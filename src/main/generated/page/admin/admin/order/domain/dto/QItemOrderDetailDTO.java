package page.admin.admin.order.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * page.admin.admin.order.domain.dto.QItemOrderDetailDTO is a Querydsl Projection type for ItemOrderDetailDTO
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemOrderDetailDTO extends ConstructorExpression<ItemOrderDetailDTO> {

    private static final long serialVersionUID = 1716854799L;

    public QItemOrderDetailDTO(com.querydsl.core.types.Expression<Long> orderNo, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<Long> itemId, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<Integer> quantity, com.querydsl.core.types.Expression<Long> subtotal, com.querydsl.core.types.Expression<Double> vat, com.querydsl.core.types.Expression<String> deliveryStatus) {
        super(ItemOrderDetailDTO.class, new Class<?>[]{long.class, java.time.LocalDateTime.class, long.class, String.class, int.class, long.class, double.class, String.class}, orderNo, orderDate, itemId, itemName, quantity, subtotal, vat, deliveryStatus);
    }

}

