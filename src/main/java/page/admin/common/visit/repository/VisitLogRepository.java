package page.admin.common.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import page.admin.common.visit.domain.VisitLog;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    // 1) 특정 기간에 해당하는 방문 로그 조회
    @Query("SELECT v FROM VisitLog v WHERE v.visitTime BETWEEN :start AND :end")
    List<VisitLog> findByVisitTimeBetween(LocalDateTime start, LocalDateTime end);

    // 2) group by로 요약 조회 (예: 날짜별 count)
    //   - DB 벤더에 따라 date_format 또는 함수가 다르므로 주의!
    //   - 예시: MySQL에서는 DATE_FORMAT(v.visitTime, '%Y-%m-%d') 등을 사용할 수 있음.
    //   - H2나 다른 DB 환경이면 쿼리를 맞춰야 합니다.
    //   - 아래는 단순한 JPQL 로는 어려우므로 네이티브 쿼리로 예시를 보여드립니다.

    @Query(value = """
            SELECT TO_CHAR(visit_time, 'YYYY-MM-DD') as visitDate,
                   COUNT(*) as visitCount
            FROM visit_log
            WHERE visit_time BETWEEN :start AND :end
            GROUP BY TO_CHAR(visit_time, 'YYYY-MM-DD')
            ORDER BY TO_CHAR(visit_time, 'YYYY-MM-DD') ASC
        """, nativeQuery = true)
    List<Object[]> getDailyVisitCount(LocalDateTime start, LocalDateTime end);

}