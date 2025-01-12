package page.admin.admin.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.repository.ProductSalesRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductSalesServiceImpl implements ProductSalesService {
    private final ProductSalesRepository productSalesRepository;

    /**
     * 특정 기간 동안의 제품별 매출 집계 데이터를 페이징하여 조회합니다.
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     * @param pageable  페이징 정보
     * @return Page<ProductSalesDTO> 객체
     */
    public Page<ProductSalesDTO> getProductSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return productSalesRepository.findProductSales(startDate, endDate, pageable);
    }
}
