package page.admin.admin.financial.controller;

import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/financial/financials")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping
    public String getFinancialData(@RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "transactionType", required = false) Integer transactionType,
                                   @RequestParam(name = "startDate", required = false) String startDate,
                                   @RequestParam(name = "endDate", required = false) String endDate,
                                   @RequestParam(name = "sortField", required = false, defaultValue = "transactionDate") String sortField,
                                   @RequestParam(name = "sortDirection", required = false, defaultValue = "desc") String sortDirection,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                   Model model) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        // 필터와 정렬 조건 전달
        PagedFinancialRecordDTO pagedRecords = financialService.getFilteredRecords(keyword, transactionType, startDate, endDate, sortField, sortDirection, page, size);

        model.addAttribute("financialRecords", pagedRecords.getRecords());
        model.addAttribute("currentPage", pagedRecords.getCurrentPage());
        model.addAttribute("totalPages", pagedRecords.getTotalPages());
        model.addAttribute("startPage", pagedRecords.getStartPage());
        model.addAttribute("endPage", pagedRecords.getEndPage());
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("filters", List.of(
                new FilterOption("거래 유형", "transactionType", List.of(
                        new FilterValue(1, "구매", transactionType != null && transactionType == 1),
                        new FilterValue(2, "판매", transactionType != null && transactionType == 2)
                ))
        ));

        model.addAttribute("actionUrl", "/admin/financial/financials");
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "admin/financial/financialList";
    }

    @GetMapping("/download")
    public void downloadExcel(@RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "transactionType", required = false) Integer transactionType,
                              @RequestParam(name = "startDate", required = false) String startDate,
                              @RequestParam(name = "endDate", required = false) String endDate,
                              @RequestParam(name = "sortField", required = false, defaultValue = "transactionDate") String sortField,
                              @RequestParam(name = "sortDirection", required = false, defaultValue = "desc") String sortDirection,
                              HttpServletResponse response) {
        try {
            byte[] excelData = financialService.generateExcel(keyword, transactionType, startDate, endDate, sortField, sortDirection);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"financial_records.xlsx\"");
            response.getOutputStream().write(excelData);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
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
