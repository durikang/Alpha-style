package page.admin.common.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.admin.common.log.domain.SystemLog;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
    List<SystemLog> findByEntityName(String entityName);
    List<SystemLog> findByPerformedBy(String performedBy);
}
