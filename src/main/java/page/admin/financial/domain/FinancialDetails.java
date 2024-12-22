package page.admin.financial.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FinancialDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_details_seq")
    @SequenceGenerator(name = "financial_details_seq", sequenceName = "financial_details_seq", allocationSize = 1)
    private Long detailNo;

    @ManyToOne
    @JoinColumn(name = "record_no", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private FinancialRecord financialRecord;

    private Double sgAndAExpenses;
    private Double salesRevenue;
    private Double costOfGoodsSold;
    private Double netIncome;

    @Transient
    private Double operatingIncome;

    @PostLoad
    public void calculateOperatingIncome() {
        this.operatingIncome = (salesRevenue != null ? salesRevenue : 0.0)
                - (costOfGoodsSold != null ? costOfGoodsSold : 0.0)
                - (sgAndAExpenses != null ? sgAndAExpenses : 0.0);
    }
}
