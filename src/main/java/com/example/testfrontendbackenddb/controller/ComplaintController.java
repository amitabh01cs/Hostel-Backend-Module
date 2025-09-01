package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.dto.ComplaintResponse;
import com.example.testfrontendbackenddb.entity.Complaint;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.repository.ComplaintRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {"https://hostel-management-system-cvny.vercel.app", "http://localhost:5174"})
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private RegisterStudentRepository registerStudentRepository;

    @Autowired
    private HostelBedRepository bedRepo;

    // Utility: Get roomNo by studentId (just like GatePassController)
    private String getRoomNoForStudent(Integer studentId) {
        List<HostelBed> beds = bedRepo.findAll();
        for (HostelBed bed : beds) {
            if (bed.getStudent() != null && bed.getStudent().getId().equals(studentId) && bed.getRoom() != null) {
                return bed.getRoom().getRoomNo();
            }
        }
        return "";
    }

    // Student submits complaint (NO photo now)
    @PostMapping(consumes = {"multipart/form-data"})
    public Map<String, Object> submitComplaint(
            @RequestParam("studentId") Integer studentId,
            @RequestParam("issueDate") String issueDate,
            @RequestParam("topic") String topic,
            @RequestParam("description") String description
    ) {
        Complaint complaint = new Complaint();
        complaint.setStudentId(studentId);
        complaint.setIssueDate(java.sql.Date.valueOf(issueDate));
        complaint.setTopic(topic);
        complaint.setDescription(description);
        complaint.setStatus("New");
        complaint.setStudentClosed(false);

        Complaint saved = complaintRepository.save(complaint);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("complaintId", saved.getId());
        return resp;
    }

    // Admin fetches all complaints WITH student info (fullName, mobileNo, emailId, roomNo), with gender filter
    @GetMapping
    public List<ComplaintResponse> getAllComplaints(
            @RequestParam(value = "gender", required = false) String gender // e.g. "M", "F"
    ) {
        List<Complaint> complaints = complaintRepository.findAll();
        List<ComplaintResponse> responses = new ArrayList<>();
        for (Complaint c : complaints) {
            if (Boolean.TRUE.equals(c.getStudentClosed())) continue;
            RegisterStudent student = registerStudentRepository.findById(c.getStudentId()).orElse(null);
            // Gender filter: if gender param is present, skip if student not matching
            if (gender != null && student != null && !gender.equalsIgnoreCase(student.getGender())) {
                continue;
            }
            String fullName = student != null ? student.getFullName() : "";
            String mobileNo = student != null ? student.getMobileNo() : "";
            String emailId = student != null ? student.getEmailId() : "";

            // Get roomNo using the same logic as GatePassController
            String roomNo = getRoomNoForStudent(c.getStudentId());

            responses.add(new ComplaintResponse(
                    c.getId(),
                    c.getStudentId(),
                    fullName,
                    mobileNo,
                    emailId,
                    roomNo,
                    c.getIssueDate(),
                    c.getTopic(),
                    c.getDescription(),
                    c.getStatus(),
                    null, // Photo URL removed
                    c.getFeedback(),
                    c.getStudentClosed()
            ));
        }
        return responses;
    }

    // Student fetches their own complaints
    @GetMapping("/student/{studentId}")
    public List<ComplaintResponse> getComplaintsByStudent(@PathVariable Integer studentId) {
        List<Complaint> complaints = complaintRepository.findAll();
        List<ComplaintResponse> responses = new ArrayList<>();
        for (Complaint c : complaints) {
            if (!Objects.equals(c.getStudentId(), studentId)) continue;
            RegisterStudent student = registerStudentRepository.findById(c.getStudentId()).orElse(null);
            String fullName = student != null ? student.getFullName() : "";
            String mobileNo = student != null ? student.getMobileNo() : "";
            String emailId = student != null ? student.getEmailId() : "";

            String roomNo = getRoomNoForStudent(c.getStudentId());

            responses.add(new ComplaintResponse(
                    c.getId(),
                    c.getStudentId(),
                    fullName,
                    mobileNo,
                    emailId,
                    roomNo,
                    c.getIssueDate(),
                    c.getTopic(),
                    c.getDescription(),
                    c.getStatus(),
                    null, // Photo URL removed
                    c.getFeedback(),
                    c.getStudentClosed()
            ));
        }
        return responses;
    }

    // Admin update complaint status
    @PatchMapping("/{id}/status")
    public Complaint updateComplaintStatus(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Complaint c = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        c.setStatus(req.get("status"));
        return complaintRepository.save(c);
    }

    // Student closes complaint & gives feedback
    @PatchMapping("/{id}/close")
    public Complaint closeComplaint(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Complaint c = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        c.setStudentClosed(true);
        if (req.containsKey("feedback")) {
            c.setFeedback(req.get("feedback"));
        }
        c.setStatus("Closed");
        return complaintRepository.save(c);
    }
}