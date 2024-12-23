package page.admin.financial.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import page.admin.financial.domain.FinancialRecord;
import page.admin.financial.repository.FinancialRecordRepository;

@Service
public class FinancialServiceImpl implements FinancialService {

    private final FinancialRecordRepository financialRecordRepository;

    public FinancialServiceImpl(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    @Override
    public Page<FinancialRecord> getFilteredRecords(String keyword, Integer transactionType, int page) {
        // PageRequest.of(page, size)를 사용해 페이징 처리
        return financialRecordRepository.findByFilters(keyword, transactionType, PageRequest.of(page, 10));
    }
}
