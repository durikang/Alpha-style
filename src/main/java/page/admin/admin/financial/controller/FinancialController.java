package page.admin.admin.financial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.admin.financial.domain.FinancialRecord;
import page.admin.admin.financial.domain.dto.FilterOption;
import page.admin.admin.financial.domain.dto.FilterValue;
import page.admin.admin.financial.service.FinancialService;

import java.util.List;

@Controller
@RequestMapping("/admin/financial/financials")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping
    public String getFinancialData(@RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "transactionType", required = false) Integer transactionType,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   Model model) {
        Page<FinancialRecord> records = financialService.getFilteredRecords(keyword, transactionType, page);

        int currentPage = page + 1;
        int totalPages = records.getTotalPages();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        model.addAttribute("financialRecords", records.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        model.addAttribute("filters", List.of(
                new FilterOption("거래 유형", "transactionType", List.of(
                        new FilterValue(1, "구매", transactionType != null && transactionType == 1),
                        new FilterValue(2, "판매", transactionType != null && transactionType == 2)
                ))
        ));

        model.addAttribute("actionUrl", "/financial/financials");

        return "admin/financial/financialList";
    }

    @GetMapping("/test")
    public String test() {
        return "admin/financial/financialTestPage";
    }

    @GetMapping("/options")
    public String options() {
        return "admin/financial/financialOpton";
    }

}
