package page.admin.admin.order.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SalesSearchRequest {
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

    // 하나의 검색어만
    private String keyword;
    private String deliveryStatus;

    // Querydsl 검색용 필드 (필수 아님)
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
