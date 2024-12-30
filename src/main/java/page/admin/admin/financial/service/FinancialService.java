package page.admin.admin.financial.service;

import org.springframework.data.domain.Page;
import page.admin.admin.financial.domain.FinancialRecord;

public interface FinancialService {
    Page<FinancialRecord> getFilteredRecords(String keyword, Integer transactionType, int page);
}
