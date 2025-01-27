package page.admin.common.visit.service;

import page.admin.common.visit.dto.DailyVisitCountDTO;

import java.util.List;

public interface VisitLogService {

    // 방문 로그 저장
    void saveVisitLog(String ip, String userAgent);

    // 특정 기간의 날짜별 방문 통계
    List<DailyVisitCountDTO> getDailyVisitCountForPeriod(String startDate, String endDate);

    // 향후 다른 통계 메서드 등 필요하면 추가
}
