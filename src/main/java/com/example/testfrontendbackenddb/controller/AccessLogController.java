package com.example.testfrontendbackenddb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@CrossOrigin(origins = {"http://localhost:5174", "https://hostel-management-system-cvny.vercel.app"})
@RestController
@RequestMapping("/api/access-log")
public class AccessLogController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    // Add a new access log entry
    @PostMapping
    public ResponseEntity<?> saveAccessLog(@RequestBody Map<String, Object> logData, HttpServletRequest request) {
        try {
            // Required fields: userId, userEmail, userType, status
            String userId = String.valueOf(logData.getOrDefault("userId", ""));
            String userEmail = String.valueOf(logData.getOrDefault("userEmail", ""));
            String userType = String.valueOf(logData.getOrDefault("userType", ""));
            String status = String.valueOf(logData.getOrDefault("status", ""));

            // Get real client IP from headers or remoteAddr
            String ipAddress = getClientIp(request);

            // loginTime bana lo java.sql.Timestamp se (server time use karo)
            Timestamp loginTime = Timestamp.valueOf(LocalDateTime.now());

            if (userId.isEmpty() || userEmail.isEmpty() || userType.isEmpty() || ipAddress.isEmpty() || status.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "All fields are required"));
            }

            String sql = "INSERT INTO access_log (user_id, user_email, user_type, ip_address, login_time, status) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, userEmail, userType, ipAddress, loginTime, status);

            return ResponseEntity.ok(Map.of("success", true, "message", "Access log saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error saving access log"));
        }
    }

    // Get all access logs (optional, for admin viewing)
    @GetMapping
    public ResponseEntity<?> getAllLogs() {
        String sql = "SELECT * FROM access_log ORDER BY login_time DESC";
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    // Clear all logs (delete all entries)
    @DeleteMapping
    public ResponseEntity<?> clearLogs() {
        try {
            jdbcTemplate.update("DELETE FROM access_log");
            return ResponseEntity.ok(Map.of("success", true, "message", "All access logs cleared."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error clearing logs."));
        }
    }
}