package page.admin.admin.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.SalesSearchRequest;
import page.admin.admin.order.repository.OrderDetailRepository;

@Service
@RequiredArgsConstructor
public class SalesManagerServiceImpl implements SalesManagerService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Page<OrderDetail> getSalesRecords(SalesSearchRequest searchRequest, Pageable pageable) {
        return orderDetailRepository.searchSalesRecords(searchRequest, pageable);
    }
}
