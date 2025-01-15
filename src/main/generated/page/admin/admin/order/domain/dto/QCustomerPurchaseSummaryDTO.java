package page.admin.admin.order.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * page.admin.admin.order.domain.dto.QCustomerPurchaseSummaryDTO is a Querydsl Projection type for CustomerPurchaseSummaryDTO
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCustomerPurchaseSummaryDTO extends ConstructorExpression<CustomerPurchaseSummaryDTO> {

    private static final long serialVersionUID = 534237876L;

    public QCustomerPurchaseSummaryDTO(com.querydsl.core.types.Expression<String> customerId, com.querydsl.core.types.Expression<String> customerName, com.querydsl.core.types.Expression<Long> purchaseCount, com.querydsl.core.types.Expression<Long> totalAmount) {
        super(CustomerPurchaseSummaryDTO.class, new Class<?>[]{String.class, String.class, long.class, long.class}, customerId, customerName, purchaseCount, totalAmount);
    }

}

