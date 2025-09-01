package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.GatePassRequest;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.repository.GatePassRequestRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gate-pass")
public class GatePassController {

    @Autowired
    private GatePassRequestRepository gatePassRepo;

    @Autowired
    private RegisterStudentRepository studentRepo;

    @Autowired
    private HostelBedRepository bedRepo; // For roomNo mapping

    // Utility: Get roomNo by studentId
    private String getRoomNoForStudent(Integer studentId) {
        List<HostelBed> beds = bedRepo.findAll();
        for (HostelBed bed : beds) {
            if (bed.getStudent() != null && bed.getStudent().getId().equals(studentId) && bed.getRoom() != null) {
                return bed.getRoom().getRoomNo();
            }
        }
        return "";
    }

    @PostMapping("/request")
    public GatePassRequest createRequest(@RequestBody Map<String, Object> reqMap) {
        Integer studentId = Integer.parseInt(reqMap.get("studentId").toString());
        RegisterStudent student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Date fromTime = null, toTime = null;
        String passType = (String) reqMap.get("passType");
        try {
            if ("HOUR".equalsIgnoreCase(passType)) {
                String dateStr = (String) reqMap.get("date");
                String fromStr = (String) reqMap.get("from");
                String toStr = (String) reqMap.get("to");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                fromTime = sdf.parse(dateStr + " " + fromStr);
                toTime = sdf.parse(dateStr + " " + toStr);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fromTime = sdf.parse((String) reqMap.get("from"));
                toTime = sdf.parse((String) reqMap.get("to"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid date/time format in request: " + e.getMessage());
        }

        GatePassRequest req = new GatePassRequest();
        req.setStudent(student);
        req.setPassType(passType);
        req.setFromTime(fromTime);
        req.setToTime(toTime);
        req.setReason((String) reqMap.get("purpose"));
        req.setStatus("pending");
        req.setCreatedAt(new Date());
        req.setPlaceToVisit((String) reqMap.get("placeToVisit"));

        return gatePassRepo.save(req);
    }

    @GetMapping("/student/{studentId}")
    public List<Map<String, Object>> getByStudent(@PathVariable Integer studentId) {
        List<GatePassRequest> passes = gatePassRepo.findByStudentIdOrderByCreatedAtDesc(studentId);
        return passes.stream().map(gp -> {
            RegisterStudent s = gp.getStudent();
            Map<String, Object> studentMap = new HashMap<>();
            if (s != null) {
                studentMap.put("id", s.getId());
                studentMap.put("fullName", s.getFullName());
                studentMap.put("branch", s.getBranch());
                studentMap.put("yearOfStudy", s.getYearOfStudy());
                studentMap.put("mobileNo", s.getMobileNo());
                studentMap.put("rollNo", s.getEmailId());
                studentMap.put("gender", s.getGender());
                studentMap.put("roomNo", getRoomNoForStudent(s.getId()));
                studentMap.put("photoPath", s.getPhotoPath());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", gp.getId());
            map.put("passType", gp.getPassType());
            map.put("fromTime", gp.getFromTime());
            map.put("toTime", gp.getToTime());
            map.put("reason", gp.getReason());
            map.put("status", gp.getStatus());
            map.put("createdAt", gp.getCreatedAt());
            map.put("student", studentMap);
            map.put("address", gp.getPlaceToVisit());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/student/{studentId}/counts")
    public ResponseEntity<?> getGatePassCounts(@PathVariable Integer studentId) {
        int pending = gatePassRepo.countByStudentIdAndStatus(studentId, "pending");
        int approved = gatePassRepo.countByStudentIdAndStatus(studentId, "approved");
        int rejected = gatePassRepo.countByStudentIdAndStatus(studentId, "rejected");
        int total = gatePassRepo.countByStudentId(studentId);

        Map<String, Integer> counts = new HashMap<>();
        counts.put("pending", pending);
        counts.put("approved", approved);
        counts.put("rejected", rejected);
        counts.put("total", total);

        return ResponseEntity.ok(counts);
    }

    // GET /api/gate-pass/all?gender=M or ?gender=F or no param for all
    @GetMapping("/all")
    public List<Map<String, Object>> getAll(@RequestParam(value = "gender", required = false) String gender) {
        List<GatePassRequest> all = gatePassRepo.findAll();
        if (gender != null && !gender.isEmpty()) {
            all = all.stream()
                    .filter(gp -> gp.getStudent() != null && gender.equalsIgnoreCase(gp.getStudent().getGender()))
                    .collect(Collectors.toList());
        }
        // Map response with roomNo
        return all.stream().map(gp -> {
            RegisterStudent s = gp.getStudent();
            Map<String, Object> studentMap = new HashMap<>();
            if (s != null) {
                studentMap.put("id", s.getId());
                studentMap.put("fullName", s.getFullName());
                studentMap.put("branch", s.getBranch());
                studentMap.put("yearOfStudy", s.getYearOfStudy());
                studentMap.put("mobileNo", s.getMobileNo());
                studentMap.put("rollNo", s.getEmailId());
                studentMap.put("gender", s.getGender());
                studentMap.put("roomNo", getRoomNoForStudent(s.getId()));
                studentMap.put("photoPath", s.getPhotoPath());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", gp.getId());
            map.put("passType", gp.getPassType());
            map.put("fromTime", gp.getFromTime());
            map.put("toTime", gp.getToTime());
            map.put("reason", gp.getReason());
            map.put("status", gp.getStatus());
            map.put("createdAt", gp.getCreatedAt());
            map.put("student", studentMap);
            map.put("address", gp.getPlaceToVisit());
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/{id}/status")
    public GatePassRequest updateStatus(@PathVariable Integer id, @RequestParam String status) {
        GatePassRequest req = gatePassRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(status);
        if ("approved".equalsIgnoreCase(status)) req.setApprovedAt(new Date());
        return gatePassRepo.save(req);
    }

    // NEW: Admin/Warden can edit gate pass time
    @PostMapping("/{id}/edit-time")
    public ResponseEntity<?> editPassTime(@PathVariable Integer id, @RequestBody Map<String, String> timeMap) {
        GatePassRequest req = gatePassRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            if (timeMap.containsKey("fromTime")) {
                req.setFromTime(sdf.parse(timeMap.get("fromTime")));
            }
            if (timeMap.containsKey("toTime")) {
                req.setToTime(sdf.parse(timeMap.get("toTime")));
            }
            gatePassRepo.save(req);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}