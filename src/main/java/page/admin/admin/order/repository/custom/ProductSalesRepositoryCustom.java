package page.admin.admin.order.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.domain.dto.SalesSearchRequest;

public interface ProductSalesRepositoryCustom {

    // 기존: findProductSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    // -> 아래처럼 "SalesSearchRequest" 전체를 파라미터로 받는 메서드 추가
    Page<ProductSalesDTO> findProductSales(SalesSearchRequest searchRequest, Pageable pageable);
}
