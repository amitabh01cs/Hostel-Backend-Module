package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.model.UserActivityLog;
import com.example.testfrontendbackenddb.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5174", "https://hostel-management-system-cvny.vercel.app"})
@RestController
@RequestMapping("/api/activity-log")
public class UserActivityLogController {

    @Autowired
    private UserActivityLogRepository logRepository;

    // Helper to get real client IP address
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    // Add a new activity log entry
    @PostMapping
    public ResponseEntity<?> saveActivityLog(@RequestBody Map<String, Object> logData, HttpServletRequest request) {
        try {
            String userId = String.valueOf(logData.getOrDefault("userId", ""));
            String userEmail = String.valueOf(logData.getOrDefault("userEmail", ""));
            String userType = String.valueOf(logData.getOrDefault("userType", ""));
            String actionType = String.valueOf(logData.getOrDefault("actionType", "VISIT"));
            String pageUrl = String.valueOf(logData.getOrDefault("pageUrl", ""));
            String actionDescription = String.valueOf(logData.getOrDefault("actionDescription", ""));

            String ipAddress = getClientIp(request);
            LocalDateTime activityTime = LocalDateTime.now();

            if (userId.isEmpty() || userEmail.isEmpty() || userType.isEmpty() || pageUrl.isEmpty() || actionType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "All fields are required"));
            }

            UserActivityLog log = new UserActivityLog();
            log.setUserId(userId);
            log.setUserEmail(userEmail);
            log.setUserType(userType);
            log.setIpAddress(ipAddress);
            log.setActionType(actionType);
            log.setPageUrl(pageUrl);
            log.setActionDescription(actionDescription);
            log.setActivityTime(activityTime);

            logRepository.save(log);

            return ResponseEntity.ok(Map.of("success", true, "message", "Activity log saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error saving activity log"));
        }
    }

    // Get all activity logs
    @GetMapping
    public List<UserActivityLog> getAllLogs() {
        return logRepository.findAll();
    }

    // Clear all logs
    @DeleteMapping
    public ResponseEntity<?> clearLogs() {
        try {
            logRepository.deleteAll();
            return ResponseEntity.ok(Map.of("success", true, "message", "All activity logs cleared."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error clearing logs."));
        }
    }
}
