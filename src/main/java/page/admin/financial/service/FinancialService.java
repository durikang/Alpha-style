package page.admin.financial.service;

import org.springframework.data.domain.Page;
import page.admin.financial.domain.FinancialRecord;

public interface FinancialService {
    Page<FinancialRecord> getFilteredRecords(String keyword, Integer transactionType, int page);
}
