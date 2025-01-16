package page.admin.admin.financial.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordDTO {
    private String formattedDate;
    private Integer year;
    private Integer month;
    private Integer day;
    private String transactionType;
    private String productName;
    private Double quantity;
    private Double unitPrice;
    private Double supplyAmount;
    private Double vat;
    private Double totalAmount;

    public void setFormattedDate() {
        this.formattedDate = String.format("%04d-%02d-%02d", year, month, day);
    }
}


