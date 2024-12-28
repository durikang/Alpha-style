package page.admin.admin.order.domain;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productNo;  // 상품 번호
    private Integer quantity; // 수량
    private Double subtotal;  // 소계
}