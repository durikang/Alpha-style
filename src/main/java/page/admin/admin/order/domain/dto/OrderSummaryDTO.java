package page.admin.admin.order.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderSummaryDTO {
    private Long itemId;             // 상품 ID
    private String itemName;         // 상품명
    private String categoryName;     // 카테고리명
    private Long totalQuantity;      // 총 주문 수량
    private double totalAmount;      // 총 주문 금액
    private Long shippingQuantity;   // 배송중 수량
    private Long completedQuantity;  // 배송완료 수량
    private String sellerName;       // 판매자 이름
    private LocalDateTime createdDate;   // 등록일

    // QueryDSL에서 사용하기 위한 생성자에 @QueryProjection 추가
    @QueryProjection
    public OrderSummaryDTO(Long itemId, String itemName, Long totalQuantity, double totalAmount, Long shippingQuantity, Long completedQuantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.shippingQuantity = shippingQuantity;
        this.completedQuantity = completedQuantity;
    }

    @QueryProjection
    public OrderSummaryDTO(Long itemId, String itemName, String categoryName,
                           Long totalQuantity, Double totalAmount,
                           Long shippingQuantity, Long completedQuantity,
                           String sellerName, LocalDateTime createdDate) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.categoryName = categoryName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.shippingQuantity = shippingQuantity;
        this.completedQuantity = completedQuantity;
        this.sellerName = sellerName;
        this.createdDate = createdDate;
    }

    // 포맷팅된 총 주문 금액 반환
    public String getFormattedTotalAmount() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(totalAmount);
    }
}
