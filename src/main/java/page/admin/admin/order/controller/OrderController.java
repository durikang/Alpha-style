package page.admin.admin.order.controller;

import com.querydsl.core.Tuple;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.item.domain.Item;
import page.admin.admin.order.domain.dto.CustomerPurchaseSummaryDTO;
import page.admin.admin.order.domain.dto.ItemOrderDetailDTO;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.admin.item.service.DeliveryCodeService;
import page.admin.admin.member.domain.Member;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.admin.order.service.OrderService;
import page.admin.common.utils.Alert;
import page.admin.common.utils.DateUtils;
import page.admin.common.utils.PageableUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DeliveryCodeService deliveryCodeService;
    private final ItemRepository itemRepository;

    @GetMapping("/list")
    public String getBuyerPurchaseList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "sortField", defaultValue = "totalAmount") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            Model model) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "날짜 형식이 잘못되었습니다.");
            return "admin/order/buyerPurchaseList";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerPurchaseSummaryDTO> summaries = orderService.getBuyerOrderSummaries(
                keyword, startDate, endDate, pageable, sortField, sortDirection
        );

        int totalPages = summaries.getTotalPages();
        int currentPage = summaries.getNumber() + 1; // 페이지는 0부터 시작하므로 +1
        int groupSize = 10; // 한 그룹에 표시할 페이지 수
        int currentGroup = (currentPage - 1) / groupSize + 1; // 현재 페이지 그룹
        int startPage = (currentGroup - 1) * groupSize + 1; // 그룹 시작 페이지
        int endPage = Math.min(startPage + groupSize - 1, totalPages); // 그룹 종료 페이지

        // 고객 정보를 Map 리스트로 변환
        List<Map<String, Object>> customers = summaries.getContent().stream()
                .map(summary -> {
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", summary.getCustomerId());
                    customerMap.put("name", summary.getCustomerName());
                    return customerMap;
                })
                .collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("customerSummaries", summaries);
        model.addAttribute("customers", customers); // 고객 선택 드롭다운용 데이터 추가
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("currentGroup", currentGroup);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);

        return "admin/order/buyerPurchaseList";
    }

    @GetMapping("/list/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBuyerPurchaseListAjax(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "sortField", defaultValue = "totalAmount") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

        log.info("Ajax 요청 - page: {}, size: {}, keyword: {}, startDate: {}, endDate: {}, sortField: {}, sortDirection: {}",
                page, size, keyword, startDateStr, endDateStr, sortField, sortDirection);

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
            }
        } catch (DateTimeParseException e) {
            log.error("400 에러 - 잘못된 날짜 형식", e);
            return ResponseEntity.badRequest().body(Map.of("error", "날짜 형식이 잘못되었습니다."));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerPurchaseSummaryDTO> summaries = orderService.getBuyerOrderSummaries(
                keyword, startDate, endDate, pageable, sortField, sortDirection
        );

        if (summaries.isEmpty()) {
            log.warn("검색 결과가 없습니다.");
        }

        List<Map<String, Object>> customers = summaries.getContent().stream()
                .map(summary -> {
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", summary.getCustomerId());
                    customerMap.put("name", summary.getCustomerName());
                    return customerMap;
                })
                .collect(Collectors.toList());

        log.info("Ajax 응답 - 고객 수: {}", customers.size());
        return ResponseEntity.ok(Map.of("customers", customers));
    }




    @GetMapping("/customerSummary")
    public String getCustomerSummary(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "sortField", defaultValue = "totalAmount") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            Model model) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "날짜 형식이 잘못되었습니다.");
            return "admin/order/buyerPurchaseList";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerPurchaseSummaryDTO> summaries = orderService.getBuyerOrderSummaries(
                keyword, startDate, endDate, pageable, sortField, sortDirection);

        int totalPages = summaries.getTotalPages();
        int currentPage = summaries.getNumber() + 1;
        int groupSize = 10;
        int currentGroup = (currentPage - 1) / groupSize + 1;
        int startPage = (currentGroup - 1) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        model.addAttribute("customerSummaries", summaries != null ? summaries : Page.empty());
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("currentGroup", currentGroup);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);

        return "admin/order/buyerPurchaseList";
    }



    // 기존 downloadCSV 메서드 및 기타 메서드들...

    /**
     * CSV 다운로드 기능
     */
    @GetMapping("/download")
    public void downloadCSV(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "sortField", defaultValue = "orderDate") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            HttpServletResponse response
    ) {
        log.info("CSV 다운로드 요청 - keyword: {}, startDate: {}, endDate: {}, sortField: {}, sortDirection: {}",
                keyword, startDateStr, endDateStr, sortField, sortDirection);

        // 날짜 문자열을 LocalDateTime으로 변환
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = DateUtils.parseLocalDateTime(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = DateUtils.parseLocalDateTime(endDateStr);
                // 종료 시간을 하루의 마지막으로 설정
                endDate = endDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
        } catch (Exception e) {
            log.error("날짜 형식 변환 오류: startDateStr={}, endDateStr={}", startDateStr, endDateStr, e);
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 날짜 형식입니다.");
            } catch (IOException ioException) {
                log.error("오류 메시지 전송 실패", ioException);
            }
            return;
        }

        List<Order> orders;
        try {
            Pageable pageable = PageableUtils.createPageRequest(0, Integer.MAX_VALUE, sortField, sortDirection);
            orders = orderService.getOrdersWithFilters(keyword, startDate, endDate, pageable, sortField, sortDirection).getContent();
        } catch (Exception e) {
            log.error("주문 데이터 조회 중 오류 발생", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "주문 데이터를 조회하는 중 오류가 발생했습니다.");
            } catch (IOException ioException) {
                log.error("오류 메시지 전송 실패", ioException);
            }
            return;
        }

        // CSV 파일 생성
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/csv;charset=UTF-8");
            String fileName = "주문내역.csv";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            // BOM 추가 (Excel 호환성 개선)
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outputStreamWriter);
            writer.write('\uFEFF'); // UTF-8 BOM

            writer.println("주문 번호,회원 이름,주문 날짜,총 금액,배송 상태");

            // DateTimeFormatter 사용
            DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Order order : orders) {
                String formattedDate = (order.getOrderDate() != null) ? order.getOrderDate().format(sdf) : "";
                // CSV 형식에 맞게 데이터를 이스케이프 처리 (콤마 포함 시 따옴표로 감싸기)
                String userName = escapeCsv(order.getUser().getUsername());
                String deliveryStatus = escapeCsv(order.getDeliveryStatus());

                String line = String.format("%d,%s,%s,%.2f,%s",
                        order.getOrderNo(),
                        userName,
                        formattedDate,
                        order.getTotalAmount(),
                        deliveryStatus);
                writer.println(line);
            }

            writer.flush();
            writer.close();
            log.info("CSV 파일 다운로드 완료 - 총 주문 수: {}", orders.size());
        } catch (IOException e) {
            log.error("CSV 파일 생성 중 오류 발생", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "CSV 파일을 생성하는 중 오류가 발생했습니다.");
            } catch (IOException ioException) {
                log.error("오류 메시지 전송 실패", ioException);
            }
        }
    }


    /**
     * CSV 필드 이스케이프 처리
     * @param field CSV 필드 값
     * @return 이스케이프 처리된 필드 값
     */
    private String escapeCsv(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }




    /**
     * 제품별 주문 현황 페이지
     */
    @GetMapping("/summaryList")
    public String getOrderSummaries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "itemId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {

        Pageable pageable = PageableUtils.createPageRequest(page, size, sortBy, sortDir);
        Page<OrderSummaryDTO> summaries = orderService.getOrderSummariesWithPaging(pageable, keyword);

        int totalPages = summaries.getTotalPages();
        int currentPage = page + 1;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (summaries.isEmpty()) {
            System.out.println("Order summaries 데이터가 비어 있습니다.");
        }

        model.addAttribute("orderSummaries", summaries.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/order/orderSummaryList";
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/details/{id}")
    public String getOrderDetails(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            throw new IllegalArgumentException("해당 주문 정보를 찾을 수 없습니다.");
        }

        Member customer = order.getUser();
        List<OrderDetail> orderDetails = order.getOrderDetails();
        List<String> deliveryStatuses = deliveryCodeService.findAll()
                .stream()
                .map(code -> code.getDisplayName())
                .toList();

        model.addAttribute("order", order);
        model.addAttribute("customer", customer);
        model.addAttribute("details", orderDetails);
        model.addAttribute("deliveryStatuses", deliveryStatuses);

        return "admin/order/orderDetails";
    }


    @PostMapping("/update")
    public String updateOrderStatus(
            @RequestParam("orderNo") Long orderNo,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderNo, status);
            redirectAttributes.addFlashAttribute("alert", new Alert("배송 상태가 성공적으로 업데이트되었습니다.", Alert.AlertType.SUCCESS));
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alert", new Alert("잘못된 요청입니다. 주문 번호를 확인하세요.", Alert.AlertType.ERROR));
        } catch (Exception e) {
            log.error("배송 상태 업데이트 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("alert", new Alert("배송 상태를 업데이트하는 중 오류가 발생했습니다.", Alert.AlertType.ERROR));
        }
        return "redirect:/admin/orders/details/" + orderNo;
    }

    /**
     * 상품별 주문 상세 (itemId 기준)
     */
    @GetMapping("/itemDetails/{itemId}")
    public String getItemOrderDetails(@PathVariable("itemId") Long itemId,
                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      @RequestParam(name = "startDate", required = false) String startDateStr,
                                      @RequestParam(name = "endDate", required = false) String endDateStr,
                                      Model model) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. itemId=" + itemId));

        Pageable pageable = PageableUtils.createPageRequest(page, size, "orderDate", "desc");

        // 날짜 필터링
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr, formatter).atStartOfDay();
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr, formatter).atTime(23, 59, 59, 999999999);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.", e);
        }

        Page<ItemOrderDetailDTO> itemOrderDetails = orderService.getItemOrderDetails(itemId, pageable, startDate, endDate);

        int totalPages = itemOrderDetails.getTotalPages();
        int currentPage = page + 1;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        model.addAttribute("item", item);
        model.addAttribute("itemOrderDetails", itemOrderDetails.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);

        return "admin/order/itemOrderDetails";
    }


    /**
     * 차트 분석 Ajax용
     */
    @GetMapping("/itemDetails/analyze")
    @ResponseBody
    public Map<String, Object> analyzeItemDetails(
            @RequestParam("itemId") Long itemId,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "chartType", required = false) String chartType
    ) {
        // 1) 날짜 파싱 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                LocalDate startLocalDate = LocalDate.parse(startDateStr, formatter);
                startDate = startLocalDate.atStartOfDay();
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                LocalDate endLocalDate = LocalDate.parse(endDateStr, formatter);
                endDate = endLocalDate.atTime(23, 59, 59, 999999999);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd", e);
        }

        List<Tuple> rows = orderService.analyzeItemSalesByQuarter(itemId, startDate, endDate);

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Tuple tuple : rows) {
            String quarterLabel = tuple.get(0, String.class);    // "2023-Q1" 등
            Number total = tuple.get(1, Number.class);           // 수량 합계

            int sumQty = (total != null) ? total.intValue() : 0;
            labels.add(quarterLabel);
            values.add(sumQty);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        result.put("title", "Item " + itemId + " 분기별 판매량 분석");
        result.put("chartType", chartType); // 필요하다면

        return result;

    }

    /**
     * 고객 분석
     *
     * @param customerIdStr 고객 ID (문자열 형식)
     * @param startDateStr 분석 시작 날짜 (yyyy-MM-dd)
     * @param endDateStr 분석 종료 날짜 (yyyy-MM-dd)
     * @return 분석 결과 (JSON 형식)
     */
    @GetMapping("/analyze")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeCustomerPurchases(
            @RequestParam("customerId") String customerIdStr,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {

        log.info("analyzeCustomerPurchases 요청: customerId={}, startDate={}, endDate={}", customerIdStr, startDateStr, endDateStr);

        Map<String, Object> result = new HashMap<>();

        try {
            // 고객 ID 전처리
            if (customerIdStr.startsWith("user_")) {
                customerIdStr = customerIdStr.replace("user_", "");
            }

            Long customerId;
            try {
                customerId = Long.parseLong(customerIdStr);
            } catch (NumberFormatException e) {
                log.error("잘못된 customerId 형식: {}", customerIdStr, e);
                result.put("error", "고객 ID 형식이 잘못되었습니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 날짜 파싱
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            try {
                if (startDateStr != null) {
                    startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
                }
                if (endDateStr != null) {
                    endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
                }
            } catch (Exception e) {
                log.error("날짜 형식 오류: startDate={}, endDate={}", startDateStr, endDateStr, e);
                result.put("error", "날짜 형식이 잘못되었습니다. yyyy-MM-dd 형식을 사용해주세요.");
                return ResponseEntity.badRequest().body(result);
            }

            // 데이터 조회
            List<Tuple> data = orderService.analyzeCustomerPurchases(customerId, startDate, endDate);
            if (data == null || data.isEmpty()) {
                log.warn("분석 데이터가 없습니다. customerId={}, startDate={}, endDate={}", customerId, startDate, endDate);
                result.put("error", "분석 결과가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            // 데이터 처리
            List<String> labels = new ArrayList<>();
            List<Long> values = new ArrayList<>();
            String customerName = ""; // 회원 이름 저장

            for (Tuple tuple : data) {
                // 회원 이름 가져오기
                customerName = tuple.get(6, String.class); // 회원 이름 (String 타입)

                // 날짜 가져오기 (Null 체크)
                String formattedDate = tuple.get(0, String.class);
                labels.add(formattedDate != null ? formattedDate : "N/A");

                // 총 금액 가져오기 (Long 타입으로 수정)
                Long totalAmount = tuple.get(2, Long.class); // 타입을 Long으로 명시
                values.add(totalAmount != null ? totalAmount : 0L);
            }


            // 결과 데이터 구성
            result.put("labels", labels);
            result.put("values", values);
            result.put("title", String.format("%s님의 구매 분석 결과 (%s ~ %s)",
                    customerName,
                    startDateStr != null ? startDateStr : "전체",
                    endDateStr != null ? endDateStr : "전체"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("분석 중 예외 발생: customerId={}, startDate={}, endDate={}", customerIdStr, startDateStr, endDateStr, e);
            result.put("error", "분석 중 문제가 발생했습니다. 관리자에게 문의하세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }



    /**
     * 매출 분석
     */
    @GetMapping("/analyzeSalesSummary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeSalesSummary(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam(value = "chartType", defaultValue = "line") String chartType) {

        Map<String, Object> response = new HashMap<>();

        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + "T23:59:59");

            if ("pie".equals(chartType)) {
                // 파이 차트용 데이터 (큰 카테고리별, 서브 카테고리별 매출)
                Map<String, Object> categorySales = orderService.analyzeCategorySales(startDate, endDate);
                response.putAll(categorySales);
            } else {
                // 일반 차트 데이터 (라인/막대)
                List<Tuple> results = orderService.analyzeSalesSummary(startDate, endDate);

                List<String> dates = new ArrayList<>();
                List<Long> totalSales = new ArrayList<>();
                List<Double> averageSales = new ArrayList<>();

                for (Tuple result : results) {
                    dates.add(result.get(0, String.class));
                    totalSales.add(result.get(1, Long.class));
                    averageSales.add(result.get(2, Double.class));
                }

                response.put("dates", dates);
                response.put("totalSales", totalSales);
                response.put("averageSales", averageSales);
                response.put("title", "총 매출 분석 (" + startDateStr + " ~ " + endDateStr + ")");
            }

            return ResponseEntity.ok(response);

        } catch (DateTimeParseException e) {
            response.put("error", "날짜 형식이 잘못되었습니다.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "분석 중 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
