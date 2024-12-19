package page.admin.aop;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import page.admin.common.BaseEntity;
import page.admin.log.domain.SystemLog;
import page.admin.log.repository.SystemLogRepository;
import page.admin.member.domain.Member;

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

        // 엔티티 ID 가져오기
        if (result != null) {
            try {
                entityId = (Long) result.getClass().getMethod("getId").invoke(result);
            } catch (Exception e) {
                log.warn("Unable to retrieve ID from entity: {}", result.getClass().getSimpleName());
            }
        }

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpSession) {
                HttpSession session = (HttpSession) arg;
                Member loginUser = (Member) session.getAttribute("loginUser");
                if (loginUser != null) {
                    performedBy = loginUser.getUsername(); // 로그인 사용자 이름 또는 ID
                    break;
                }
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
