package com.example.testfrontendbackenddb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:4000"})
@RestController
@RequestMapping("/api/student")
public class StudentLoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // STUDENT LOGIN API
    @PostMapping("/login")
    public ResponseEntity<?> loginStudent(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String emailOrMobile = loginData.get("email");
        String password = loginData.get("password");

        // Input validation
        if (emailOrMobile == null || password == null
                || emailOrMobile.trim().isEmpty() || password.trim().isEmpty()) {
            logAccess("", emailOrMobile, "student", request.getRemoteAddr(), "failure");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Email/Mobile and Password required"));
        }

        // Query to match either email or mobile, and password
        String sql = "SELECT * FROM register_student WHERE (email_id = ? OR mobile_no = ?) AND student_password = ?";
        List<Map<String, Object>> students = jdbcTemplate.queryForList(sql, emailOrMobile, emailOrMobile, password);

        String ipAddress = request.getRemoteAddr();
        String userType = "student";
        String userId = "";
        String userEmail = emailOrMobile;

        if (!students.isEmpty()) {
            Map<String, Object> student = students.get(0);

            // Fetch room/bed info for this student (room number, bed id, room type)
            Integer studentId = (Integer) student.get("id");
            String roomNo = "";
            String bedId = "";
            String roomType = "";

            String bedSql = "SELECT b.bed_id, r.room_no, r.type FROM hostel_bed b " +
                    "JOIN hostel_room r ON b.room_id = r.id " +
                    "WHERE b.student_id = ?";
            List<Map<String, Object>> bedInfos = jdbcTemplate.queryForList(bedSql, studentId);
            if (!bedInfos.isEmpty()) {
                roomNo = (String) bedInfos.get(0).get("room_no");
                bedId = (String) bedInfos.get(0).get("bed_id");
                roomType = (String) bedInfos.get(0).get("type");
            }

            // Fetch roommate info (other students in same room, except this student)
            List<Map<String, Object>> roommates = List.of();
            if (!roomNo.isEmpty()) {
                String roommateSql = "SELECT s.id, s.full_name FROM register_student s " +
                        "JOIN hostel_bed b ON s.id = b.student_id " +
                        "JOIN hostel_room r ON b.room_id = r.id " +
                        "WHERE r.room_no = ? AND s.id <> ?";
                roommates = jdbcTemplate.queryForList(roommateSql, roomNo, studentId);
            }

            // Prepare response using HashMap
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("email", student.get("email_id"));
            response.put("mobile_no", student.get("mobile_no"));
            response.put("studentId", student.get("id"));
            response.put("full_name", student.get("full_name"));
            response.put("course", student.get("course"));
            response.put("room", roomNo);
            response.put("bed", bedId);
            response.put("roomType", roomType);
            response.put("roommates", roommates);

            userId = String.valueOf(student.get("id"));
            userEmail = String.valueOf(student.get("email_id"));

            // Save success access log
            logAccess(userId, userEmail, userType, ipAddress, "success");

            return ResponseEntity.ok(response);
        } else {
            // Save failure access log
            logAccess(userId, userEmail, userType, ipAddress, "failure");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email/mobile or password"));
        }
    }

    // Helper method to log access (DATE/TIME FIXED)
    private void logAccess(String userId, String userEmail, String userType, String ipAddress, String status) {
        Timestamp loginTime = Timestamp.valueOf(LocalDateTime.now());
        String sql = "INSERT INTO access_log (user_id, user_email, user_type, ip_address, login_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, userEmail, userType, ipAddress, loginTime, status);
    }

    // GET STUDENT PROFILE BY ID
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getStudentProfileById(@PathVariable("id") int id) {
        String sql = "SELECT * FROM register_student WHERE id = ?";
        List<Map<String, Object>> students = jdbcTemplate.queryForList(sql, id);

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found"));
        }
        return ResponseEntity.ok(students.get(0));
    }

    // ROOMMATES API (in case you want to call separately)
    @GetMapping("/roommates")
    public ResponseEntity<?> getRoommates(
            @RequestParam String room,
            @RequestParam Integer excludeStudentId
    ) {
        String sql = "SELECT s.id, s.full_name FROM register_student s " +
                "JOIN hostel_bed b ON s.id = b.student_id " +
                "JOIN hostel_room r ON b.room_id = r.id " +
                "WHERE r.room_no = ? AND s.id <> ?";
        List<Map<String, Object>> roommates = jdbcTemplate.queryForList(sql, room, excludeStudentId);
        return ResponseEntity.ok(roommates);
    }
}