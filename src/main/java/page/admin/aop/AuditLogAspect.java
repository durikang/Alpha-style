package page.admin.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import page.admin.common.BaseEntity;
import page.admin.log.domain.SystemLog;
import page.admin.log.repository.SystemLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final SystemLogRepository systemLogRepository;

    // 저장 (등록) 로그
    @AfterReturning(value = "execution(* page.admin..*Service.save*(..))", returning = "result")
    public void logSaveAction(JoinPoint joinPoint, Object result) {
        logAction("등록", joinPoint, result);
    }

    // 수정 로그
    @AfterReturning(value = "execution(* page.admin..*Service.update*(..))", returning = "result")
    public void logUpdateAction(JoinPoint joinPoint, Object result) {
        logAction("수정", joinPoint, result);
    }

    // 삭제 로그
    @After(value = "execution(* page.admin..*Service.delete*(..))")
    public void logDeleteAction(JoinPoint joinPoint) {
        logAction("삭제", joinPoint, null);
    }

    // 공통 로그 기록 메서드
    private void logAction(String actionType, JoinPoint joinPoint, Object result) {
        String entityName = joinPoint.getTarget().getClass().getSimpleName(); // 엔티티 이름
        String performedBy = "unknown"; // 기본값
        Long entityId = null;

        // 엔티티 ID와 수행자 가져오기
        if (result != null && result instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) result;
            entityId = entity.getId();
        }

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String) { // 예: 세션에서 유저 정보
                performedBy = (String) arg;
                break;
            }
        }

        SystemLog logEntry = new SystemLog(
                entityName,
                entityId,
                actionType,
                performedBy,
                actionType + " 작업이 수행되었습니다."
        );

        systemLogRepository.save(logEntry);
        log.info("SystemLog 저장 완료: {}", logEntry);
    }
}
