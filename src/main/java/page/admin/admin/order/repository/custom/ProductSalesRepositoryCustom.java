package page.admin.admin.order.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.dto.ProductSalesDTO;

import java.time.LocalDateTime;

public interface ProductSalesRepositoryCustom {
    Page<ProductSalesDTO> findProductSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
