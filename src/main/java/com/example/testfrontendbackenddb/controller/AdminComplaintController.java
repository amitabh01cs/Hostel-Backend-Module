package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.AdminComplaint;
import com.example.testfrontendbackenddb.entity.AdminUser;
import com.example.testfrontendbackenddb.repository.AdminComplaintRepository;
import com.example.testfrontendbackenddb.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@CrossOrigin(origins = {"https://hostel-management-system-cvny.vercel.app", "http://localhost:5174"})
@RestController
@RequestMapping("/api/admin-complaints")
public class AdminComplaintController {

    @Autowired
    private AdminComplaintRepository adminComplaintRepo;

    @Autowired
    private AdminUserRepository adminUserRepo;

    private final String UPLOAD_DIR = "uploads/admin-complaints/";

    @PostMapping(consumes = {"multipart/form-data"})
    public Map<String, Object> submitAdminComplaint(
            @RequestParam("adminId") Integer adminId,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.write(filePath, file.getBytes());
                fileUrl = "/files/admin-complaints/" + filename;
            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        AdminComplaint complaint = new AdminComplaint(
                adminId,
                category,
                description,
                fileUrl,
                new Date(),
                "New"
        );
        AdminComplaint saved = adminComplaintRepo.save(complaint);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("id", saved.getId());
        return resp;
    }

    // DTO for response
    public static class AdminComplaintWithName {
        public Long id;
        public Integer adminId;
        public String adminName;
        public String category;
        public String description;
        public String fileUrl;
        public Date createdAt;
        public String status;
    }

    // Super Admin: Get all admin complaints with admin name (from admin_user)
    @GetMapping
    public List<AdminComplaintWithName> getAllAdminComplaints(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        List<AdminComplaint> list = adminComplaintRepo.findAll();
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // Map adminId -> admin name from admin_user
        Map<Integer, String> idToName = new HashMap<>();
        for (AdminUser admin : adminUserRepo.findAll()) {
            idToName.put(admin.getId(), admin.getAdminName());
        }

        Calendar cal = Calendar.getInstance();
        List<AdminComplaintWithName> result = new ArrayList<>();
        for (AdminComplaint c : list) {
            // Filter by month/year if provided
            if (month != null && year != null) {
                cal.setTime(c.getCreatedAt());
                int complaintMonth = cal.get(Calendar.MONTH) + 1;
                int complaintYear = cal.get(Calendar.YEAR);
                if (complaintMonth != month || complaintYear != year) continue;
            }

            AdminComplaintWithName dto = new AdminComplaintWithName();
            dto.id = c.getId();
            dto.adminId = c.getAdminId();
            dto.adminName = idToName.getOrDefault(c.getAdminId(), "");
            dto.category = c.getCategory();
            dto.description = c.getDescription();
            dto.fileUrl = c.getFileUrl();
            dto.createdAt = c.getCreatedAt();
            dto.status = c.getStatus();
            result.add(dto);
        }
        return result;
    }
}