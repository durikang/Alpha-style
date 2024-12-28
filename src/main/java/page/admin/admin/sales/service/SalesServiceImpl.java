package page.admin.admin.sales.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import page.admin.admin.order.repository.OrderRepository;
import page.admin.admin.sales.domain.dto.SalesRecordDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final OrderRepository orderRepository;

    @Override
    public Page<SalesRecordDto> getSalesRecords(Pageable pageable, String keyword, LocalDate startDate, LocalDate endDate) {
        // OrderRepository 호출로 데이터 조회
        return orderRepository.findSalesRecordsWithPaging(pageable, keyword, startDate, endDate);
    }
}
