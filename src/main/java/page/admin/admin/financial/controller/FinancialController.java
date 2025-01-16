package page.admin.admin.financial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.admin.financial.domain.dto.FilterOption;
import page.admin.admin.financial.domain.dto.FilterValue;
import page.admin.admin.financial.domain.dto.PagedFinancialRecordDTO;
import page.admin.admin.financial.service.FinancialService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/financial/financials")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping
    public String getFinancialData(@RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "transactionType", required = false) String transactionType,
                                   @RequestParam(name = "startDate", required = false) String startDate,
                                   @RequestParam(name = "endDate", required = false) String endDate,
                                   @RequestParam(name = "sortField", required = false, defaultValue = "transactionDate") String sortField,
                                   @RequestParam(name = "sortDirection", required = false, defaultValue = "desc") String sortDirection,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                   Model model) {

        // 페이지와 사이즈 값 검증
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        // transactionType을 Integer로 변환
        Integer transactionTypeInt = null;
        if ("구매".equals(transactionType)) {
            transactionTypeInt = 1;
        } else if ("판매".equals(transactionType)) {
            transactionTypeInt = 2;
        }

        // 서비스 호출
        PagedFinancialRecordDTO pagedRecords = financialService.getFilteredRecords(
                keyword, transactionTypeInt, startDate, endDate, sortField, sortDirection, page, size
        );

        // URL에 필터와 정렬 조건 포함 (URL 인코딩 포함)
        String actionUrl = String.format(
                "/admin/financial/financials?keyword=%s&transactionType=%s&startDate=%s&endDate=%s&sortField=%s&sortDirection=%s",
                URLEncoder.encode(keyword != null ? keyword : "", StandardCharsets.UTF_8),
                URLEncoder.encode(transactionType != null ? transactionType : "", StandardCharsets.UTF_8),
                URLEncoder.encode(startDate != null ? startDate : "", StandardCharsets.UTF_8),
                URLEncoder.encode(endDate != null ? endDate : "", StandardCharsets.UTF_8),
                sortField,
                sortDirection
        );

        // 모델 데이터 설정
        model.addAttribute("financialRecords", pagedRecords.getRecords());
        model.addAttribute("currentPage", pagedRecords.getCurrentPage());
        model.addAttribute("totalPages", pagedRecords.getTotalPages());
        model.addAttribute("startPage", pagedRecords.getStartPage());
        model.addAttribute("endPage", pagedRecords.getEndPage());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("actionUrl", actionUrl);

        // 필터 추가
        model.addAttribute("filters", List.of(
                new FilterOption("거래 유형", "transactionType", List.of(
                        new FilterValue(1, "구매", "구매".equals(transactionType)),
                        new FilterValue(2, "판매", "판매".equals(transactionType))
                ))
        ));

        return "admin/financial/financialList";
    }






    @GetMapping("/analytics")
    public String analytics() {
        return "admin/financial/financialDataPage";
    }

    @GetMapping("/options")
    public String options() {
        return "admin/financial/financialOpton";
    }

}
