package page.admin.admin.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.domain.dto.SalesSearchRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesService {

    /**
     * 특정 기간 동안의 제품별 매출 집계 데이터를 조회합니다.
     *
     * @param request 조회 시작일
     * @param pageable 조회 종료일
     * @return 제품별 매출 데이터 목록
     */
    Page<ProductSalesDTO> getProductSales(SalesSearchRequest request, Pageable pageable);

}
