package page.admin.admin.order.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.text.DecimalFormat;

@Data
public class CustomerPurchaseSummaryDTO {
    private String customerId;
    private String customerName;
    private Long purchaseCount;
    private Long totalAmount;

    // QueryDSL용 생성자
    @QueryProjection
    public CustomerPurchaseSummaryDTO(String customerId, String customerName, Long purchaseCount, Long totalAmount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.purchaseCount = purchaseCount;
        this.totalAmount = totalAmount;
    }

    // 포맷팅된 총 구매 금액 반환
    public String getFormattedTotalAmount() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(totalAmount);
    }
}
