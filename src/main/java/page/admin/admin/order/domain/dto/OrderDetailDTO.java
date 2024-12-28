package page.admin.admin.order.domain.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long orderNo;          // 주문 번호
    private Long itemId;           // 상품 번호
    private String itemName;       // 상품 이름
    private Integer quantity;      // 수량
    private Integer purchasePrice; // 매입가
    private Integer salePrice;     // 판매가
    private Long subtotal;         // 결제 금액
    private String orderDate;      // 주문 날짜 (포맷된 문자열)
    private String deliveryStatus; // 배송 상태
    private String mainImageUrl;   // 상품 이미지 URL
}
