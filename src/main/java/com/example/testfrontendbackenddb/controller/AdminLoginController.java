package com.example.testfrontendbackenddb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5174" , "https://hostel-management-system-cvny.vercel.app"})
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Admin login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String adminType = loginData.get("adminType");

        if (username == null || password == null || adminType == null
                || username.trim().isEmpty() || password.trim().isEmpty() || adminType.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "All fields required"));
        }

        // Make admin_type case-insensitive for matching
        String sql = "SELECT id, admin_name, admin_emailId, admin_mobile_no, admin_type FROM admin_user WHERE username = ? AND password = ? AND LOWER(admin_type) = LOWER(?)";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, username, password, adminType);

        if (!results.isEmpty()) {
            Map<String, Object> admin = results.get(0);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("adminId", admin.get("id"));
            response.put("name", admin.get("admin_name"));
            response.put("email", admin.get("admin_emailId"));
            response.put("mobile", admin.get("admin_mobile_no"));
            response.put("adminType", admin.get("admin_type"));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }

    // Endpoint to check if an admin with given email exists (for frontend verification)
    @GetMapping("/exists")
    public ResponseEntity<?> adminExists(@RequestParam String email) {
        String sql = "SELECT COUNT(*) FROM admin_user WHERE admin_emailId = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        boolean exists = count != null && count > 0;
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}