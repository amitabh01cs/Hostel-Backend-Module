package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.EmergencyReport;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.AdminUser;
import com.example.testfrontendbackenddb.repository.EmergencyReportRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import com.example.testfrontendbackenddb.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = {"https://hostel-management-system-cvny.vercel.app", "http://localhost:5174"})
@RestController
@RequestMapping("/api/emergency-reports")
public class AdminEmergencyReportController {

    @Autowired
    private EmergencyReportRepository emergencyReportRepo;

    @Autowired
    private RegisterStudentRepository studentRepo;

    @Autowired
    private AdminUserRepository adminUserRepo;

    // DTO for response
    public static class EmergencyReportWithNames {
        public Integer id;
        public Integer adminId;
        public String adminName;
        public Integer studentId;
        public String studentName;
        public String transcript;
        public Date createdAt;
        public String status;
    }

    // Submit Emergency Report (adminId, studentId, transcript)
    @PostMapping
    public Map<String, Object> submitEmergencyReport(
            @RequestBody Map<String, Object> body
    ) {
        Integer adminId = (Integer) body.get("adminId");
        Integer studentId = null;
        try {
            Object stuId = body.get("studentId");
            if (stuId instanceof Integer) {
                studentId = (Integer) stuId;
            } else if (stuId != null) {
                studentId = Integer.parseInt(stuId.toString());
            }
        } catch (Exception ignored) {}

        String transcript = (String) body.get("transcript");

        Map<String, Object> resp = new HashMap<>();
        if (adminId == null || studentId == null || transcript == null || transcript.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "Missing required parameters");
            return resp;
        }

        Optional<RegisterStudent> studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "Student not found");
            return resp;
        }

        EmergencyReport report = new EmergencyReport();
        report.setAdminId(adminId);
        report.setStudentId(studentId);
        report.setTranscript(transcript.trim());
        report.setCreatedAt(new Date());
        report.setStatus("New");

        EmergencyReport saved = emergencyReportRepo.save(report);

        resp.put("success", true);
        resp.put("id", saved.getId());
        return resp;
    }

    // Get all emergency reports with admin and student names (latest first), with optional gender/month/year filter
    @GetMapping
    public List<EmergencyReportWithNames> getAllEmergencyReports(
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        List<EmergencyReport> reports = emergencyReportRepo.findAll();
        reports.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // Map studentId -> name and gender
        Map<Integer, String> studentIdToName = new HashMap<>();
        Map<Integer, String> studentIdToGender = new HashMap<>();
        for (RegisterStudent s : studentRepo.findAll()) {
            studentIdToName.put(s.getId(), s.getFullName());
            if (s.getGender() != null)
                studentIdToGender.put(s.getId(), s.getGender());
        }

        // Map adminId -> adminName
        Map<Integer, String> adminIdToName = new HashMap<>();
        for (AdminUser a : adminUserRepo.findAll()) {
            adminIdToName.put(a.getId(), a.getAdminName());
        }

        // Filter by gender if needed
        Set<Integer> allowedStudentIds = null;
        if (gender != null && !gender.isBlank()) {
            allowedStudentIds = new HashSet<>();
            for (Map.Entry<Integer, String> entry : studentIdToGender.entrySet()) {
                if (gender.equalsIgnoreCase(entry.getValue())) {
                    allowedStudentIds.add(entry.getKey());
                }
            }
        }

        List<EmergencyReportWithNames> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (EmergencyReport r : reports) {
            if (allowedStudentIds != null && !allowedStudentIds.contains(r.getStudentId())) continue;

            // Filter by month and year if provided
            if (month != null && year != null) {
                cal.setTime(r.getCreatedAt());
                int reportMonth = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based!
                int reportYear = cal.get(Calendar.YEAR);
                if (reportMonth != month || reportYear != year) continue;
            }

            EmergencyReportWithNames dto = new EmergencyReportWithNames();
            dto.id = r.getId();
            dto.adminId = r.getAdminId();
            dto.adminName = adminIdToName.getOrDefault(r.getAdminId(), "");
            dto.studentId = r.getStudentId();
            dto.studentName = studentIdToName.getOrDefault(r.getStudentId(), "");
            dto.transcript = r.getTranscript();
            dto.createdAt = r.getCreatedAt();
            dto.status = r.getStatus();
            result.add(dto);
        }
        return result;
    }
}