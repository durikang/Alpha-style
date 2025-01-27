package page.admin.common.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyVisitCountDTO {
    private String date;    // 예: "2025-01-20"
    private Long count;     // 해당 날짜 방문 수
}