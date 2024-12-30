package page.admin.admin.financial.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.admin.admin.item.domain.Item;
import page.admin.admin.member.domain.Member;

@Entity
@Data
@NoArgsConstructor
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_record_seq")
    @SequenceGenerator(name = "financial_record_seq", sequenceName = "financial_record_seq", allocationSize = 1)
    private Long recordNo;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member user; // 구매자 또는 판매자

    @Column(name = "fin_year") // 예약어 문제를 피하기 위해 "fin_year"로 변경
    private Integer year;

    @Column(name = "fin_month") // 예약어 문제를 피하기 위해 "fin_month"로 변경
    private Integer month;

    @Column(name = "fin_day") // 예약어 문제를 피하기 위해 "fin_day"로 변경
    private Integer day;

    private Integer transactionType; // 1: 구매, 2: 판매
    private Double quantity;
    private Double unitPrice;
    private Double supplyAmount;
    private Double vat;

    private String productName;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @ManyToOne
    @JoinColumn(name = "tax_code", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private TaxType taxType;

    @PrePersist
    public void calculateSupplyAmountAndVAT() {
        this.supplyAmount = (quantity != null && unitPrice != null) ? quantity * unitPrice : 0.0;
        this.vat = (supplyAmount != null) ? supplyAmount * 0.1 : 0.0; // 부가가치세 10%
    }
}
