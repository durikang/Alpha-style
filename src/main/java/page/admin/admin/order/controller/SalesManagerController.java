package page.admin.admin.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import page.admin.admin.order.domain.dto.ProductSalesDTO;
import page.admin.admin.order.domain.dto.SalesSearchRequest;
import page.admin.admin.order.service.ProductSalesService;
import page.admin.common.service.CsvExportService;
import page.admin.common.utils.DateUtils;
import page.admin.common.utils.PageableUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/pd-manager")
@RequiredArgsConstructor
public class SalesManagerController {

    private final ProductSalesService productSalesService;
    private final CsvExportService csvExportService;

    /**
     * 제품별 판매 기록 조회 페이지
     */
    @GetMapping("/product-sales-list")
    public String productSalesList(Model model,
                                   @ModelAttribute("searchRequest") SalesSearchRequest searchRequest,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageableUtils.createPageRequest(page, size, "totalAmount", "desc");

        // startDate와 endDate가 null일 경우 기본값 설정
        if (searchRequest.getStartDate() == null || searchRequest.getEndDate() == null) {
            LocalDateTime now = LocalDateTime.now();
            searchRequest.setStartDate(now.minusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0)); // 최근 한 달 시작
            searchRequest.setEndDate(now.withHour(23).withMinute(59).withSecond(59).withNano(999999999)); // 오늘 종료
        }

        Page<ProductSalesDTO> salesData = productSalesService.getProductSales(
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                pageable
        );

        // 템플릿에 필요한 데이터 설정
        model.addAttribute("salesList", salesData.getContent());
        model.addAttribute("currentPage", salesData.getNumber());
        model.addAttribute("totalPages", salesData.getTotalPages());
        model.addAttribute("totalElements", salesData.getTotalElements());

        return "admin/sales/product-sales-list";
    }


    /**
     * CSV 다운로드 기능
     */
    @GetMapping("/product-sales-list/downloadCSV")
    public ResponseEntity<?> downloadProductSalesCsv(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            HttpServletResponse response
    ) {
        try {
            LocalDateTime startDate = parseDate(startDateStr, false);
            LocalDateTime endDate = parseDate(endDateStr, true);

            List<ProductSalesDTO> salesData = productSalesService.getProductSales(
                    startDate, endDate, Pageable.unpaged()
            ).getContent();

            String fileName = "product_sales_report.csv";
            String header = "Order No, Item Name, Seller ID, Quantity, Sale Price, VAT, Total Amount, Delivery Status";
            List<String> dataLines = salesData.stream()
                    .map(dto -> String.join(",",
                            String.valueOf(dto.getOrderNo()),
                            csvExportService.escapeCsv(dto.getItemName()),
                            csvExportService.escapeCsv(dto.getSellerId()),
                            String.valueOf(dto.getQuantity()),
                            dto.getFormattedSalePrice(),
                            dto.getFormattedVAT(),
                            dto.getFormattedTotalAmount(),
                            dto.getDeliveryStatus()
                    ))
                    .collect(Collectors.toList());

            csvExportService.exportToCsv(response, fileName, header, dataLines);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("CSV 파일 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CSV 생성 오류가 발생했습니다."));
        }
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 변환
     */
    private LocalDateTime parseDate(String dateStr, boolean isEndDate) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime date = DateUtils.parseLocalDateTime(dateStr);
            if (isEndDate) {
                return date.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            return date;
        } catch (Exception e) {
            log.error("날짜 변환 오류: {}", dateStr, e);
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }
    }
}
