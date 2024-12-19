package page.admin.log.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import page.admin.log.domain.SystemLog;
import page.admin.log.service.SystemLogService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    public List<SystemLog> getAllLogs() {
        return systemLogService.getAllLogs();
    }

    @GetMapping("/entity/{name}")
    public List<SystemLog> getLogsByEntityName(@PathVariable String name) {
        return systemLogService.getLogsByEntityName(name);
    }

    @GetMapping("/user/{userId}")
    public List<SystemLog> getLogsByUser(@PathVariable String userId) {
        return systemLogService.getLogsByUser(userId);
    }
}
