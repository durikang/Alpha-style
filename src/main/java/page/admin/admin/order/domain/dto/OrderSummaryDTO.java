package page.admin.admin.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long itemId;           // 상품 ID
    private String itemName;       // 상품명
    private Long totalQuantity;    // 총 주문 수량
    private double totalAmount;    // 총 주문 금액
    private Long shippingQuantity; // 배송중 수량
    private Long completedQuantity;// 배송완료 수량

    // 포맷팅된 총 주문 금액 반환
    public String getFormattedTotalAmount() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(totalAmount);
    }
}
