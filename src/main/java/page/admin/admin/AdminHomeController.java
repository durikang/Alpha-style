package page.admin.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import page.admin.common.visit.dto.DailyVisitCountDTO;
import page.admin.common.visit.service.VisitLogService;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminHomeController {

    private final VisitLogService visitLogService;

    @GetMapping("/adminHome")
    public String adminHome(HttpSession session, Model model) {
        if (!hasAdminAccess(session)) {
            return "redirect:/login";
        }

        // 1) 예: 최근 7일(주간) 방문 통계
        //    startDate : 오늘로부터 6일 전
        //    endDate   : 오늘
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<DailyVisitCountDTO> visitStats = visitLogService.getDailyVisitCountForPeriod(
                weekAgo.toString(),  // "2025-01-21" 형태
                today.toString()     // "2025-01-27" 형태
        );

        // 2) 모델에 담아서 Thymeleaf로 전달
        model.addAttribute("visitStats", visitStats);

        return "admin/adminHome";
    }

    private boolean hasAdminAccess(HttpSession session) {
        Object sessionInfo = session.getAttribute("loginMember");
        if (sessionInfo instanceof LoginSessionInfo) {
            LoginSessionInfo loginInfo = (LoginSessionInfo) sessionInfo;
            return "admin".equalsIgnoreCase(loginInfo.getRole());
        }
        return false;
    }

    @GetMapping("/adminHome/visit-stats")
    @ResponseBody
    public List<DailyVisitCountDTO> getVisitStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        return visitLogService.getDailyVisitCountForPeriod(
                weekAgo.toString(),
                today.toString()
        );
    }

}

