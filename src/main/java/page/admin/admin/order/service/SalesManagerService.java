package page.admin.admin.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.SalesSearchRequest;

public interface SalesManagerService {
    Page<OrderDetail> getSalesRecords(SalesSearchRequest searchRequest, Pageable pageable);
}
