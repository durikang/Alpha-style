package page.admin.admin.sales.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class SalesRecordDto {

    private Long orderNo;
    private LocalDateTime orderDate; // Date에서 LocalDateTime으로 변경
    private String username;
    private String itemName;
    private Integer quantity;
    private Integer salePrice;
    private Long totalAmount;
    private String deliveryStatus;

    public SalesRecordDto(Long orderNo, LocalDateTime orderDate, String username, String itemName,
                          Integer quantity, Integer salePrice, Long totalAmount, String deliveryStatus) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.username = username;
        this.itemName = itemName;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.totalAmount = totalAmount;
        this.deliveryStatus = deliveryStatus;
    }
}


