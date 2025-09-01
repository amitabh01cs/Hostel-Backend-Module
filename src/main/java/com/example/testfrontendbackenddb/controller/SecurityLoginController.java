package com.example.testfrontendbackenddb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/api/security")
public class SecurityLoginController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> loginSecurity(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "All fields required"));
        }

        // Get full info for localStorage (like admin)
        String sql = "SELECT id, security_name, security_emailId, security_mobileNo, username FROM security_user WHERE username = ? AND password = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, username, password);

        if (!results.isEmpty()) {
            Map<String, Object> security = results.get(0);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("securityId", security.get("id"));
            response.put("name", security.get("security_name"));
            response.put("email", security.get("security_emailId"));
            response.put("mobile", security.get("security_mobileNo"));
            response.put("username", security.get("username"));
            // You can add more fields as needed
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }

    // Security exists endpoint (for frontend check)
    @GetMapping("/exists")
    public ResponseEntity<?> securityExists(@RequestParam String email) {
        String sql = "SELECT COUNT(*) FROM security_user WHERE security_emailId = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        boolean exists = count != null && count > 0;
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}