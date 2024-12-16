package hello.itemservice.domain.order;

import hello.itemservice.domain.item.Item;
import lombok.Data;

@Data
public class OrderDetail {
    private Long orderDetailNo; // 주문 상세 번호
    private Long orderNo;       // 주문 번호 (외래 키)
    private Long productNo;     // 제품 번호 (외래 키)
    private Integer quantity;   // 수량
    private Long subtotal;    // 소계 (계산 필요)
    private Item item;          // 상품 객체 (연관 관계)

    // subtotal 계산 메서드
    public void calculateSubtotal() {
        if (item != null) {
            long price = item.getPrice() != null ? item.getPrice() : 0;
            int qty = quantity != null ? quantity : 0;
            this.subtotal = (price * qty);
        } else {
            this.subtotal = 0L;
        }
    }
}

