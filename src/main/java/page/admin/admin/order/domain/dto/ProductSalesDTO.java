package page.admin.admin.order.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class ProductSalesDTO {

    private Long orderNo;
    private LocalDateTime orderDate;
    private String itemName;
    private String sellerId;
    private Integer quantity;
    private Integer salePrice;
    private Double vat;
    private Double totalAmount;
    private String deliveryStatus;


    // 새로운 생성자 추가 (JPQL과 매개변수 일치)
    public ProductSalesDTO(Long orderNo, String itemName, String sellerId, Integer quantity, Integer salePrice, Double totalAmount) {
        this.orderNo = orderNo;
        this.itemName = itemName;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.totalAmount = totalAmount;
        this.vat = calculateVAT(totalAmount); // VAT 계산 로직 추가
    }

    // Hibernate에서 사용하는 생성자
    public ProductSalesDTO(Long orderNo, LocalDateTime orderDate, String itemName, String sellerId,
                           Integer quantity, Integer salePrice, Double vat, Double totalAmount, String deliveryStatus) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.itemName = itemName;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.vat = vat;
        this.totalAmount = totalAmount;
        this.deliveryStatus = deliveryStatus;
    }

    // 포맷팅 메서드
    public String getFormattedSalePrice() {
        return NumberFormat.getInstance(Locale.KOREA).format(salePrice) + " 원";
    }

    public String getFormattedVAT() {
        if (vat == null) {
            return "0 원";
        }
        return NumberFormat.getInstance(Locale.KOREA).format(vat) + " 원";
    }
    // VAT 계산 메서드 (예: 10% VAT 적용)
    private Double calculateVAT(Double totalAmount) {
        return totalAmount != null ? totalAmount * 0.1 : 0.0;
    }
    public String getFormattedTotalAmount() {
        return NumberFormat.getInstance(Locale.KOREA).format(totalAmount) + " 원";
    }

    // 추가: 날짜 포맷팅 메서드
    public String getFormattedOrderDate() {
        if (orderDate == null) {
            return "N/A";
        }
        return orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

