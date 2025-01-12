package page.admin.admin.order.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.SalesSearchRequest;

public interface OrderDetailCustomRepository {
    Page<OrderDetail> searchSalesRecords(SalesSearchRequest searchRequest, Pageable pageable);
}