package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.HostelAttendance;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.repository.HostelAttendanceRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/api/attendance")
public class HostelAttendanceController {

    @Autowired
    private HostelAttendanceRepository attendanceRepo;

    @Autowired
    private RegisterStudentRepository studentRepo;

    @Autowired
    private HostelBedRepository bedRepo;

    // Get filtered list of students (optionally by gender), with detailed info + roomNo
    @GetMapping("/filtered-list")
    public List<Map<String, Object>> getFilteredStudents(
            @RequestParam(value = "gender", required = false) String gender
    ) {
        List<RegisterStudent> students;
        if (gender != null && !gender.isEmpty()) {
            students = studentRepo.findByGenderIgnoreCase(gender);
        } else {
            students = studentRepo.findAll();
        }

        Map<Integer, String> studentIdToRoomNo = buildStudentRoomNoMap();
        List<Map<String, Object>> result = new ArrayList<>();

        for (RegisterStudent student : students) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", student.getId());
            map.put("fullName", student.getFullName());
            map.put("dob", student.getDob());
            map.put("age", student.getAge());
            map.put("gender", student.getGender());
            map.put("religion", student.getReligion());
            map.put("category", student.getCategory());
            map.put("nationality", student.getNationality());
            map.put("mobileNo", student.getMobileNo());
            map.put("emailId", student.getEmailId());
            map.put("aadharNo", student.getAadharNo());
            map.put("course", student.getCourse());
            map.put("semesterYear", student.getSemesterYear());
            map.put("instituteName", student.getInstituteName());
            map.put("courseName", student.getCourseName());
            map.put("branch", student.getBranch());
            map.put("yearOfStudy", student.getYearOfStudy());
            map.put("dateOfAdmission", student.getDateOfAdmission());
            map.put("hostelJoinDate", student.getHostelJoinDate());
            map.put("photoPath", student.getPhotoPath());
            map.put("fatherName", student.getFatherName());
            map.put("fatherOccupation", student.getFatherOccupation());
            map.put("fatherEducation", student.getFatherEducation());
            map.put("fatherEmail", student.getFatherEmail());
            map.put("fatherMobile", student.getFatherMobile());
            map.put("motherName", student.getMotherName());
            map.put("motherOccupation", student.getMotherOccupation());
            map.put("motherEducation", student.getMotherEducation());
            map.put("motherEmail", student.getMotherEmail());
            map.put("motherMobile", student.getMotherMobile());
            map.put("permanentAddress", student.getPermanentAddress());
            map.put("cityDistrict", student.getCityDistrict());
            map.put("state", student.getState());
            map.put("pinCode", student.getPinCode());
            map.put("phoneResidence", student.getPhoneResidence());
            map.put("phoneOffice", student.getPhoneOffice());
            map.put("officeAddress", student.getOfficeAddress());
            map.put("localGuardianName", student.getLocalGuardianName());
            map.put("localGuardianAddress", student.getLocalGuardianAddress());
            map.put("localGuardianPhone", student.getLocalGuardianPhone());
            map.put("localGuardianMobile", student.getLocalGuardianMobile());
            map.put("emergencyContactName", student.getEmergencyContactName());
            map.put("emergencyContactNo", student.getEmergencyContactNo());
            map.put("bloodGroup", student.getBloodGroup());
            map.put("seriousDisease", student.getSeriousDisease());
            map.put("regularMedication", student.getRegularMedication());
            map.put("hospitalRecord", student.getHospitalRecord());
            map.put("emergencyMedicine", student.getEmergencyMedicine());
            map.put("allergicTo", student.getAllergicTo());
            map.put("roomNo", studentIdToRoomNo.getOrDefault(student.getId(), ""));
            result.add(map);
        }
        return result;
    }

    // Save attendance with adminType-based gender restriction (case-insensitive)
    @PostMapping("/save")
    public Map<String, Object> saveAttendance(@RequestParam String adminType,
                                              @RequestBody List<Map<String, Object>> attendanceList) {
        Date today = new Date();
        String allowedGender = getGenderFromAdminType(adminType);
        if (allowedGender == null) {
            return Map.of("success", false, "message", "Invalid adminType");
        }

        List<Integer> submittedStudentIds = new ArrayList<>();
        for (Map<String, Object> att : attendanceList) {
            Integer sid = (Integer) att.get("studentId");
            if (sid != null) {
                submittedStudentIds.add(sid);
            }
        }

        List<HostelAttendance> existingAttendance = attendanceRepo.findAllByAttendanceDate(today);
        Set<Integer> alreadyMarked = new HashSet<>();
        for (HostelAttendance att : existingAttendance) {
            if (submittedStudentIds.contains(att.getStudentId())) {
                alreadyMarked.add(att.getStudentId());
            }
        }

        if (!alreadyMarked.isEmpty()) {
            return Map.of("success", false,
                    "message", "Attendance already submitted for some/all students!",
                    "alreadyMarked", alreadyMarked);
        }

        List<HostelAttendance> savedRecords = new ArrayList<>();
        for (Map<String, Object> att : attendanceList) {
            Integer sid = (Integer) att.get("studentId");
            String status = (String) att.get("status");
            RegisterStudent student = studentRepo.findById(sid).orElse(null);

            if (student == null || !allowedGender.equalsIgnoreCase(student.getGender())) {
                return Map.of("success", false,
                        "message", "You are not allowed to submit attendance for student ID: " + sid);
            }

            HostelAttendance record = new HostelAttendance();
            record.setStudentId(sid);
            record.setAttendanceDate(today);
            record.setStatus(status);
            record.setStudentYear(student.getYearOfStudy());
            record.setStudentBranch(student.getBranch());

            savedRecords.add(attendanceRepo.save(record));
        }

        return Map.of("success", true, "message", "Attendance saved!", "count", savedRecords.size());
    }

    // Get today's attendance (filtered by adminType or gender)
    @GetMapping("/today")
    public List<Map<String, Object>> todayAttendance(
            @RequestParam(required = false) String adminType,
            @RequestParam(required = false) String gender
    ) {
        Date today = new Date();
        String allowedGender = gender;
        if (allowedGender == null || allowedGender.isEmpty()) {
            allowedGender = getGenderFromAdminType(adminType);
        }
        if (allowedGender == null) return List.of();

        List<HostelAttendance> attendance = attendanceRepo.findAllByAttendanceDate(today);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<Integer, String> studentIdToRoomNo = buildStudentRoomNoMap();

        for (HostelAttendance att : attendance) {
            RegisterStudent s = studentRepo.findById(att.getStudentId()).orElse(null);
            if (s != null && allowedGender.equalsIgnoreCase(s.getGender())) {
                result.add(Map.of(
                        "id", s.getId(),
                        "name", s.getFullName(),
                        "rollNo", s.getEmailId(),
                        "course", s.getCourse(),
                        "year", att.getStudentYear(),
                        "branch", att.getStudentBranch(),
                        "gender", s.getGender(),
                        "roomNo", studentIdToRoomNo.getOrDefault(s.getId(), ""),
                        "status", att.getStatus()
                ));
            }
        }
        return result;
    }

    // Get attendance for a specific date (filtered by adminType or gender)
    @GetMapping("/date")
    public List<Map<String, Object>> byDate(
            @RequestParam("date") String date,
            @RequestParam(required = false) String adminType,
            @RequestParam(required = false) String gender
    ) {
        try {
            String allowedGender = gender;
            if (allowedGender == null || allowedGender.isEmpty()) {
                allowedGender = getGenderFromAdminType(adminType);
            }
            if (allowedGender == null) return List.of();

            Date dt = java.sql.Date.valueOf(date); // yyyy-MM-dd
            List<HostelAttendance> attendance = attendanceRepo.findAllByAttendanceDate(dt);
            List<Map<String, Object>> result = new ArrayList<>();
            Map<Integer, String> studentIdToRoomNo = buildStudentRoomNoMap();

            for (HostelAttendance att : attendance) {
                RegisterStudent s = studentRepo.findById(att.getStudentId()).orElse(null);
                if (s != null && allowedGender.equalsIgnoreCase(s.getGender())) {
                    result.add(Map.of(
                            "id", s.getId(),
                            "name", s.getFullName(),
                            "rollNo", s.getEmailId(),
                            "course", s.getCourse(),
                            "year", att.getStudentYear(),
                            "branch", att.getStudentBranch(),
                            "gender", s.getGender(),
                            "roomNo", studentIdToRoomNo.getOrDefault(s.getId(), ""),
                            "status", att.getStatus()
                    ));
                }
            }
            return result;
        } catch(Exception e) {
            return List.of();
        }
    }

    // Get attendance for a date range (filtered by adminType or gender)
    @GetMapping("/range")
    public List<Map<String, Object>> byDateRange(
            @RequestParam("from") String fromDate,
            @RequestParam("to") String toDate,
            @RequestParam(required = false) String adminType,
            @RequestParam(required = false) String gender
    ) {
        try {
            String allowedGender = gender;
            if (allowedGender == null || allowedGender.isEmpty()) {
                allowedGender = getGenderFromAdminType(adminType);
            }
            if (allowedGender == null) return List.of();

            Date from = java.sql.Date.valueOf(fromDate);
            Date to = java.sql.Date.valueOf(toDate);
            List<HostelAttendance> attendance = attendanceRepo.findAllByAttendanceDateBetween(from, to);
            Map<Integer, String> studentIdToRoomNo = buildStudentRoomNoMap();
            List<Map<String, Object>> result = new ArrayList<>();

            for (HostelAttendance att : attendance) {
                RegisterStudent s = studentRepo.findById(att.getStudentId()).orElse(null);
                if (s != null && allowedGender.equalsIgnoreCase(s.getGender())) {
                    result.add(Map.of(
                            "id", s.getId(),
                            "name", s.getFullName(),
                            "rollNo", s.getEmailId(),
                            "course", s.getCourse(),
                            "year", att.getStudentYear(),
                            "branch", att.getStudentBranch(),
                            "gender", s.getGender(),
                            "roomNo", studentIdToRoomNo.getOrDefault(s.getId(), ""),
                            "status", att.getStatus(),
                            "date", att.getAttendanceDate().toString()
                    ));
                }
            }
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    // Utility: Map studentId -> roomNo
    private Map<Integer, String> buildStudentRoomNoMap() {
        List<HostelBed> allBeds = bedRepo.findAll();
        Map<Integer, String> studentIdToRoomNo = new HashMap<>();
        for (HostelBed bed : allBeds) {
            if (bed.getStudent() != null && bed.getRoom() != null) {
                studentIdToRoomNo.put(bed.getStudent().getId(), bed.getRoom().getRoomNo());
            }
        }
        return studentIdToRoomNo;
    }

    // Map adminType to allowed gender
    private String getGenderFromAdminType(String adminType) {
        if ("Varahmihir".equalsIgnoreCase(adminType)) {
            return "M";
        } else if ("Maitreyi".equalsIgnoreCase(adminType)) {
            return "F";
        }
        return null;
    }
}
