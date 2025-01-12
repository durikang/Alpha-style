package page.admin.admin.order.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class SalesSearchRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    private String itemName;
    private String sellerId;
    private String deliveryStatus;  // 예) "배송중", "배송완료" 등
}