package page.admin.admin.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
     * 제품별 판매 기록 조회 페이지 (GET)
     */
    @GetMapping("/product-sales-list")
    public String productSalesList(
            Model model,
            @ModelAttribute("searchRequest") SalesSearchRequest searchRequest,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "orderNo") String sortField,
            @RequestParam(value = "direction", defaultValue = "asc") String sortDirection) {

        // 정렬 객체 생성
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색 호출
        Page<ProductSalesDTO> salesData = productSalesService.getProductSales(searchRequest, pageable);

        // 페이지네이션 계산
        int totalPages = salesData.getTotalPages();
        int currentPageIndex = salesData.getNumber();
        int blockSize = 10; // 블록 크기

        int blockStart = (currentPageIndex / blockSize) * blockSize + 1; // 블록 시작 페이지
        int blockEnd = Math.min(blockStart + blockSize - 1, totalPages); // 블록 끝 페이지

        List<Integer> pageBlock = new ArrayList<>();
        for (int i = blockStart; i <= blockEnd; i++) {
            pageBlock.add(i);
        }

        // 모델에 데이터 추가
        model.addAttribute("salesList", salesData.getContent());
        model.addAttribute("currentPage", salesData.getNumber());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", salesData.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("pagingBlock", pageBlock);

        // 검색 폼 데이터 유지
        model.addAttribute("searchRequest", searchRequest);

        return "admin/sales/product-sales-list";
    }


    /**
     * 제품별 판매 기록 검색 (POST)
     */
    @PostMapping("/product-sales/search")
    public String productSalesSearch(@ModelAttribute("searchRequest") SalesSearchRequest searchRequest,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                     @RequestParam(value = "sort", defaultValue = "orderNo") String sortField,
                                     @RequestParam(value = "direction", defaultValue = "asc") String sortDirection,
                                     Model model) {

        // 동적 정렬 처리
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색 호출
        Page<ProductSalesDTO> salesData = productSalesService.getProductSales(searchRequest, pageable);

        // 페이지네이션 블록 계산
        int totalPages = salesData.getTotalPages();
        int currentPageIndex = salesData.getNumber();
        int blockSize = 10; // 블록 크기
        int blockStart = (currentPageIndex / blockSize) * blockSize + 1;
        int blockEnd = Math.min(blockStart + blockSize - 1, totalPages);

        List<Integer> pageBlock = new ArrayList<>();
        for (int i = blockStart; i <= blockEnd; i++) {
            pageBlock.add(i);
        }

        // 모델에 데이터 추가
        model.addAttribute("salesList", salesData.getContent());
        model.addAttribute("currentPage", currentPageIndex);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", salesData.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("pagingBlock", pageBlock);
        model.addAttribute("searchRequest", searchRequest);

        return "admin/sales/product-sales-list";
    }


    /**
     * CSV 다운로드 (기존 로직)
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

            // FIXME:
            // Service가 (SalesSearchRequest, Pageable) 시그니처만 있으면,
            // 오버로드나 별도 메서드가 필요.
            // 예시로, "CSV 다운로드 시" 전용 메서드 or unpaged():
            SalesSearchRequest tempReq = new SalesSearchRequest();
            tempReq.setStartDate(startDate.toLocalDate());
            tempReq.setEndDate(endDate.toLocalDate());
            // itemName/sellerId/deliveryStatus가 필요하면 파라미터 추가

            List<ProductSalesDTO> salesData = productSalesService.getProductSales(tempReq, Pageable.unpaged()).getContent();

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
                            dto.getDeliveryStatus() != null ? dto.getDeliveryStatus() : ""
                    ))
                    .toList();

            csvExportService.exportToCsv(response, fileName, header, dataLines);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("CSV 파일 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CSV 생성 오류가 발생했습니다."));
        }
    }

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
