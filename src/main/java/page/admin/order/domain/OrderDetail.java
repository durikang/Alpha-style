package page.admin.order.domain;

import page.admin.item.domain.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_detail_seq_generator")
    @SequenceGenerator(name = "order_detail_seq_generator", sequenceName = "order_detail_seq", allocationSize = 1)
    private Long orderDetailNo; // 주문 상세 번호 (PK)

    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order; // 주문 번호 (FK)

    @ManyToOne
    @JoinColumn(name = "product_no", nullable = false)
    private Item item; // 상품 정보 (FK)

    private Integer quantity; // 수량

    private Long subtotal; // 소계 (계산된 금액)

    // 소계 계산 메서드 (값만 설정, 비즈니스 로직은 서비스에서 수행)
    public void calculateSubtotal() {
        if (item != null) {
            long price = item.getPrice() != null ? item.getPrice() : 0;
            int qty = quantity != null ? quantity : 0;
            this.subtotal = price * qty;
        } else {
            this.subtotal = 0L;
        }
    }
}
