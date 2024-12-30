package page.admin.admin.order.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.admin.item.service.DeliveryCodeService;
import page.admin.admin.member.domain.Member;
import page.admin.admin.order.domain.Order;
import page.admin.admin.order.domain.OrderDetail;
import page.admin.admin.order.domain.dto.OrderSummaryDTO;
import page.admin.admin.order.service.OrderService;
import page.admin.common.utils.PageableUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DeliveryCodeService deliveryCodeService;

    /**
     * 구매자별 구매 현황 페이지
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

        // 날짜 문자열을 Date로 변환
        Date startDate = null;
        Date endDate = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = formatter.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = formatter.parse(endDateStr);
                // 종료 시간을 하루의 마지막으로 설정
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                endDate = cal.getTime();
            }
        } catch (ParseException e) {
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

        // 필터링된 주문 데이터 조회 (정렬 파라미터 추가)
        Page<Order> orders = orderService.getOrdersWithFilters(keyword, startDate, endDate, pageable, sortField, sortDirection);

        // 페이징 속성 추가 (새로운 메서드 사용)
        PageableUtils.addPaginationAttributes(model, orders, keyword, sortField, sortDirection, startDateStr, endDateStr);

        // 모델에 데이터 추가
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);

        return "admin/order/buyerPurchaseList"; // 템플릿 경로에 맞게 수정
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

        // 날짜 문자열을 Date로 변환
        Date startDate = null;
        Date endDate = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = formatter.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = formatter.parse(endDateStr);
                // 종료 시간을 하루의 마지막으로 설정
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                endDate = cal.getTime();
            }
        } catch (ParseException e) {
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

            // SimpleDateFormat 사용
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (Order order : orders) {
                String formattedDate = (order.getOrderDate() != null) ? sdf.format(order.getOrderDate()) : "";
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
}
