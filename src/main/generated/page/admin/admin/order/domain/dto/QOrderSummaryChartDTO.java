package page.admin.admin.order.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * page.admin.admin.order.domain.dto.QOrderSummaryChartDTO is a Querydsl Projection type for OrderSummaryChartDTO
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderSummaryChartDTO extends ConstructorExpression<OrderSummaryChartDTO> {

    private static final long serialVersionUID = -1436704587L;

    public QOrderSummaryChartDTO(com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<Long> totalQuantity, com.querydsl.core.types.Expression<Double> totalAmount) {
        super(OrderSummaryChartDTO.class, new Class<?>[]{String.class, long.class, double.class}, itemName, totalQuantity, totalAmount);
    }

}

