package page.admin.log.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import page.admin.log.domain.SystemLog;
import page.admin.log.repository.SystemLogRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository systemLogRepository;

    @Override
    public List<SystemLog> getLogsByEntityName(String entityName) {
        return systemLogRepository.findByEntityName(entityName);
    }

    @Override
    public List<SystemLog> getLogsByUser(String userId) {
        return systemLogRepository.findByPerformedBy(userId);
    }

    @Override
    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAll();
    }

    @Override
    public void saveLog(SystemLog systemLog) {
        systemLogRepository.save(systemLog);
    }
}
