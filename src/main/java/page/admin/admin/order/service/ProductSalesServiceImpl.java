package page.admin.admin.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.domain.dto.SalesSearchRequest;
import page.admin.admin.order.repository.ProductSalesRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductSalesServiceImpl implements ProductSalesService {

    private final ProductSalesRepository productSalesRepository;

    @Override
    public Page<ProductSalesDTO> getProductSales(SalesSearchRequest request, Pageable pageable) {
        // LocalDate -> LocalDateTime 변환
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (start == null || end == null) {
            LocalDate now = LocalDate.now();
            if (start == null) start = now.minusMonths(1);
            if (end == null) end = now;
        }
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        // 검색 요청 객체 확장
        request.setStartDateTime(startDateTime);
        request.setEndDateTime(endDateTime);

        // QueryDSL 호출
        return productSalesRepository.findProductSales(request, pageable);
    }

}