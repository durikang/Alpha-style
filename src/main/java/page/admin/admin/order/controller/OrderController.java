package page.admin.admin.order.controller;

import com.querydsl.core.Tuple;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import page.admin.admin.item.domain.Item;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DeliveryCodeService deliveryCodeService;
    private final ItemRepository itemRepository;
    /**
     * 구매자별 상품 구매 현황 페이지
     */
    @GetMapping("/list")
    public String getBuyerPurchaseList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortField", defaultValue = "orderDate") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Model model,
            HttpServletResponse response) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        // 날짜 문자열을 LocalDateTime으로 변환
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
            return "redirect:/admin/orders/list?error=invalid_date_format";
        }

        // Pageable 객체 생성
        Pageable pageable = PageableUtils.createPageRequest(page, size, sortField, sortDirection);

        // 필터링된 주문 데이터 조회
        Page<Order> orders;
        try {
            orders = orderService.getOrdersWithFilters(keyword, startDate, endDate, pageable, sortField, sortDirection);
        } catch (Exception e) {
            log.error("주문 데이터 조회 중 오류 발생", e);
            model.addAttribute("error", "주문 데이터를 불러오는 중 오류가 발생했습니다.");
            return "admin/order/buyerPurchaseList"; // 오류 메시지를 포함한 템플릿 반환
        }

        // 페이징 속성 추가
        PageableUtils.addPaginationAttributes(model, orders, keyword, sortField, sortDirection, startDateStr, endDateStr);

        // 모델에 데이터 추가
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);

        return "admin/order/buyerPurchaseList"; // 템플릿 경로
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
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
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
                                      Model model) {

        // 1) itemId 유효성 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. itemId=" + itemId));

        // 2) 페이징 객체 생성
        Pageable pageable = PageableUtils.createPageRequest(page, size, "orderDate", "desc");

        // 3) 해당 상품에 대한 주문 상세 목록 조회 (페이징)
        Page<ItemOrderDetailDTO> itemOrderDetails = orderService.getItemOrderDetails(itemId, pageable);

        // 4) 페이징 정보 계산(화면에서 사용하는 startPage, endPage 등)
        int totalPages = itemOrderDetails.getTotalPages();
        int currentPage = page + 1;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        // 5) Model에 담아서 뷰로 전달
        model.addAttribute("item", item);
        model.addAttribute("itemOrderDetails", itemOrderDetails.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/order/itemOrderDetails";  // 뷰 템플릿 이름
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

        // 2) Service 호출 -> (날짜, 수량 합계) 튜플 목록
        List<Tuple> rows = orderService.analyzeItemSales(itemId, startDate, endDate);

        // 3) 결과를 라벨/값 리스트로 변환
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Tuple tuple : rows) {
            String dateStr = tuple.get(0, String.class);
            Number sumValue = tuple.get(1, Number.class);
            int totalQty = sumValue != null ? sumValue.intValue() : 0;

            labels.add(dateStr);
            values.add(totalQty);
        }

        // 4) JSON 응답을 위한 맵 생성
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        result.put("title", "Item " + itemId + " 분석 결과 (" + startDateStr + "~" + endDateStr + ")");
        result.put("chartType", chartType); // 필요하다면

        return result; // -> JSON
    }
}
