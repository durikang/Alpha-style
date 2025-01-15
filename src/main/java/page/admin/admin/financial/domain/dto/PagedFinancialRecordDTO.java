package page.admin.admin.financial.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedFinancialRecordDTO {
    private List<FinancialRecordDTO> records; // 현재 페이지의 데이터
    private int currentPage;
    private int totalPages;
    private int startPage;
    private int endPage;
}
