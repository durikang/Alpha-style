package page.admin.common.visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.common.visit.domain.VisitLog;
import page.admin.common.visit.dto.DailyVisitCountDTO;
import page.admin.common.visit.repository.VisitLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitLogServiceImpl implements VisitLogService {

    private final VisitLogRepository visitLogRepository;

    @Override
    public void saveVisitLog(String ip, String userAgent) {
        VisitLog log = new VisitLog(ip, userAgent);
        visitLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyVisitCountDTO> getDailyVisitCountForPeriod(String startDate, String endDate) {

        // 1) 문자열을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        // 2) LocalDate -> LocalDateTime (시작 00:00:00, 끝 23:59:59)
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);

        // 3) Repository에서 group by 된 결과 조회
        List<Object[]> result = visitLogRepository.getDailyVisitCount(startDateTime, endDateTime);

        // 4) Object[] -> DailyVisitCountDTO 변환
        //    [0]: date(String), [1]: count(Long)
        return result.stream()
                .map(obj -> new DailyVisitCountDTO(
                        (String) obj[0],
                        ((Number) obj[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
