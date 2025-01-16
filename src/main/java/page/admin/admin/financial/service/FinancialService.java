package page.admin.admin.financial.service;

import org.springframework.data.domain.Page;
import page.admin.admin.financial.domain.FinancialRecord;
import page.admin.admin.financial.domain.dto.FinancialRecordDTO;
import page.admin.admin.financial.domain.dto.PagedFinancialRecordDTO;

public interface FinancialService {
    PagedFinancialRecordDTO getFilteredRecords(String keyword, Integer transactionType, String startDate, String endDate,
                                               String sortField, String sortDirection, int page, int size);
    byte[] generateExcel(String keyword, Integer transactionType, String startDate, String endDate,
                         String sortField, String sortDirection);
}

