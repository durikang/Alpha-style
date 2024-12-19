package page.admin.log.service;

import page.admin.log.domain.SystemLog;

import java.util.List;

public interface SystemLogService {

    List<SystemLog> getLogsByEntityName(String entityName);

    List<SystemLog> getLogsByUser(String userId);

    List<SystemLog> getAllLogs();

    void saveLog(SystemLog systemLog);
}
