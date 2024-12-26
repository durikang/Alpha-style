package page.admin.sales.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import page.admin.sales.domain.dto.SalesRecordDto;
import page.admin.sales.service.SalesService;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @GetMapping
    public String getSalesRecords(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortField", required = false, defaultValue = "orderDate") String sortField,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        // 페이지 요청 생성
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // 서비스 호출로 데이터 조회
        Page<SalesRecordDto> salesPage = salesService.getSalesRecords(pageRequest, keyword, startDate, endDate);

        // 페이징 범위 계산
        int currentPage = salesPage.getNumber() + 1; // 현재 페이지 (0부터 시작하므로 +1)
        int totalPages = salesPage.getTotalPages(); // 전체 페이지 수
        int displayPages = 5; // 한 번에 표시할 페이지 수
        int startPage = Math.max(1, currentPage - displayPages / 2);
        int endPage = Math.min(totalPages, startPage + displayPages - 1);

        // 모델에 데이터 추가
        model.addAttribute("records", salesPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "sales/records";
    }
}
