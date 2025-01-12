package page.admin.admin.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import page.admin.admin.item.domain.Item;

import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_detail_seq_generator")
    @SequenceGenerator(name = "order_detail_seq_generator", sequenceName = "order_detail_seq", allocationSize = 1)
    private Long orderDetailNo;

    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;

    private Integer quantity;

    @Column(nullable = false)
    private Long subtotal;

    // 부가세 컬럼 추가
    @Column(nullable = false)
    private Double vat;

    /**
     * Subtotal 및 VAT 계산
     */
    @PrePersist
    @PreUpdate
    public void calculateAmounts() {
        calculateSubtotal();
        calculateVAT();
    }

    /**
     * Subtotal 계산: salePrice와 quantity를 기준으로 계산
     */
    public void calculateSubtotal() {
        if (item != null && item.getSalePrice() != null && quantity != null) {
            this.subtotal = (long) item.getSalePrice() * quantity;
        } else {
            this.subtotal = 0L;
        }
    }

    /**
     * VAT 계산: Subtotal의 10%
     */
    public void calculateVAT() {
        if (this.subtotal != null) {
            this.vat = this.subtotal * 0.10;
        } else {
            this.vat = 0.0;
        }
    }

    /**
     * Total Amount 계산: Subtotal + VAT
     */
    public Long getTotalAmount() {
        if (subtotal != null && vat != null) {
            return subtotal + Math.round(vat); // 반올림 처리
        }
        return 0L;
    }

    /**
     * 금액 포맷팅 메서드 (세 자리마다 쉼표)
     */
    private String formatCurrency(Long amount) {
        if (amount == null) {
            return "0 원";
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        return formatter.format(amount) + " 원";
    }

    /**
     * 포맷된 Subtotal 반환
     */
    public String getFormattedSubtotal() {
        return formatCurrency(subtotal);
    }

    /**
     * 포맷된 VAT 반환
     */
    public String getFormattedVAT() {
        return formatCurrency(Math.round(vat));
    }

    /**
     * 포맷된 Total Amount 반환
     */
    public String getFormattedTotalAmount() {
        return formatCurrency(getTotalAmount());
    }




}
