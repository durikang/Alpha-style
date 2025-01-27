package page.admin.common.visit.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import page.admin.common.visit.domain.VisitLog;
import page.admin.common.visit.repository.VisitLogRepository;
import page.admin.common.visit.service.VisitLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VisitSimulator {

    private final VisitLogService visitLogService;
    private final VisitLogRepository visitLogRepository;

    private final Random random = new Random();

    // 임의의 IP, User-Agent를 생성하기 위한 샘플 데이터
    private final String[] sampleIps = {
            "192.168.0.1", "192.168.0.50", "10.10.10.10",
            "127.0.0.1", "123.45.67.89", "8.8.8.8"
    };
    private final String[] sampleUserAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15",
            "Mozilla/5.0 (Linux; Android 10; Pixel 3) Chrome/88.0.4324.93",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15"
    };

    /**
     * 10분마다(600,000ms) 랜덤 방문 로그를 DB에 삽입하는 메서드
     */
    @Scheduled(fixedRate = 60000)  // 1분 간격
    public void simulateVisitLogs() {
        // 랜덤으로 방문 횟수를 정해봅시다(0~5)
        int numberOfVisits = random.nextInt(6);

        for (int i = 0; i < numberOfVisits; i++) {
            String randomIp = sampleIps[random.nextInt(sampleIps.length)];
            String randomUa = sampleUserAgents[random.nextInt(sampleUserAgents.length)];

            // 최근 7일 중 랜덤 날짜/시간 생성
            LocalDateTime randomVisitTime = getRandomDateTimeWithinLast7Days();

            // 서비스에서 visitTime까지 지정해서 저장하고 싶다면
            // -> 엔티티 생성 후 Repository로 직접 저장(visitLogRepository.save(...))도 가능
            // -> 혹은 Service에 visitTime을 인자로 추가한 메서드를 만들어도 됨.
            VisitLog log = new VisitLog(randomIp, randomUa);
            log.setVisitTime(randomVisitTime);

            // Repository 직접 이용 (Service에 메서드를 추가해도 OK)
            visitLogRepository.save(log);
        }

        // 일정 개수 이상 쌓이면 초기화 (예: 1000건 이상이면 전부 삭제)
        long totalCount = visitLogRepository.count();
        if (totalCount > 1000) {
            // 간단히 전부 삭제
            visitLogRepository.deleteAll();
            System.out.println("방문 로그가 1000건을 초과하여 전부 초기화했습니다.");
        }

        // 로그 출력 (디버깅용)
        System.out.println("방문 로그 " + numberOfVisits + "개 추가 (총 " + visitLogRepository.count() + "건)");
    }

    /**
     * 최근 7일(오늘 -6일 ~ 오늘) 범위 내에서 랜덤 날짜/시간 반환
     */
    private LocalDateTime getRandomDateTimeWithinLast7Days() {
        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 6일 전
        LocalDate weekAgo = today.minusDays(6);

        // weekAgo ~ today 사이의 일자를 랜덤으로 선택
        long daysRange = today.toEpochDay() - weekAgo.toEpochDay(); // ex) 7일
        long randomDayOffset = (long) (random.nextDouble() * (daysRange + 1));
        LocalDate randomDate = weekAgo.plusDays(randomDayOffset);

        // 0~23시, 0~59분, 0~59초 무작위
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);

        return randomDate.atTime(hour, minute, second);
    }
}
