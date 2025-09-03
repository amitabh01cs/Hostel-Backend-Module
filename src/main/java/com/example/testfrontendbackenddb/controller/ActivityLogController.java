package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.dto.ActivityLogDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// YE POORA CODE COPY-PASTE KAREIN
@CrossOrigin(origins = {"http://localhost:5174", "https://hostel-management-system-cvny.vercel.app"})
@RestController
@RequestMapping("/api/activity-log")
public class ActivityLogController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping
    public ResponseEntity<?> saveActivityLog(@RequestBody ActivityLogDto logData, HttpServletRequest request) {
        try {
            if (logData.getUserId() == null || logData.getUserId().isEmpty() || logData.getAction() == null || logData.getAction().isEmpty()) {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Required fields userId and action are missing."));
            }

            String ipAddress = getClientIp(request);
            Timestamp actionTime = Timestamp.valueOf(LocalDateTime.now());
            String detailsJson = logData.getDetails() != null ? objectMapper.writeValueAsString(logData.getDetails()) : null;

            String sql = "INSERT INTO activity_log (user_id, user_email, user_type, ip_address, action, page_url, details, action_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, logData.getUserId(), logData.getUserEmail(), logData.getUserType(), ipAddress, logData.getAction(), logData.getPageUrl(), detailsJson, actionTime);

            return ResponseEntity.ok(Map.of("success", true, "message", "Activity logged successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error saving activity log: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLogs() {
        String sql = "SELECT * FROM activity_log ORDER BY action_time DESC";
        List<Map<String, Object>> logs = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping
    public ResponseEntity<?> clearLogs() {
        try {
            jdbcTemplate.update("DELETE FROM activity_log");
            return ResponseEntity.ok(Map.of("success", true, "message", "All activity logs cleared."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error clearing logs."));
        }
    }
}
