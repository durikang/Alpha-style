package page.admin.admin.order.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data

public class OrderSummaryChartDTO {
    private String itemName;     // 상품명
    private Long totalQuantity;  // 총 주문 수량
    private Double totalAmount;    // 총 주문 금액

    @QueryProjection
    public OrderSummaryChartDTO(String itemName, Long totalQuantity, Double totalAmount) {
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }

}
