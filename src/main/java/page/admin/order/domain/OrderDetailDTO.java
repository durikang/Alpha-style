package page.admin.order.domain;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long orderNo;        // 주문 번호
    private Long productId;     // 상품 번호
    private String itemName;     // 상품 이름
    private Integer quantity;    // 수량
    private Long itemPrice;   // 상품 금액
    private Double subtotal;     // 결제 금액
    private String orderDate;    // 주문 날짜 (포맷된 문자열)
    private String delivaryStatus;       // 배송상태 상태
    private String mainImageUrl; // 상품 이미지 URL
}