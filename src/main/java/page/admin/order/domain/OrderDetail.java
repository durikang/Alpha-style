package page.admin.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.admin.item.domain.Item;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_detail_seq_generator")
    @SequenceGenerator(name = "order_detail_seq_generator", sequenceName = "order_detail_seq", allocationSize = 1)
    private Long orderDetailNo;

    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)) // 외래 키 제약 조건 제거
    private Item item;

    private Integer quantity;
    private Long subtotal;

    public void calculateSubtotal() {
        if (item != null) {
            this.subtotal = (item.getPrice() != null ? item.getPrice() : 0) * (quantity != null ? quantity : 0);
        }
    }
}
