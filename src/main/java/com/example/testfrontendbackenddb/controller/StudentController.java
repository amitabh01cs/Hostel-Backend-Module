package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.entity.HostelRoom;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final RegisterStudentRepository studentRepo;
    private final HostelBedRepository bedRepo;

    public StudentController(RegisterStudentRepository studentRepo, HostelBedRepository bedRepo) {
        this.studentRepo = studentRepo;
        this.bedRepo = bedRepo;
    }

    // GET STUDENT DETAILS BY ID (all details + room/bed/roommates)
    @GetMapping("/details")
    public Map<String, Object> getStudentDetails(@RequestParam Integer studentId) {
        RegisterStudent student = studentRepo.findById(studentId).orElse(null);
        if (student == null) throw new RuntimeException("Student not found");

        // Room/Bed Info
        String roomNo = "";
        String bedId = "";
        String roomType = "";
        Integer roomDbId = null;
        List<HostelBed> beds = bedRepo.findAll();
        for (HostelBed bed : beds) {
            if (bed.getStudent() != null && bed.getStudent().getId().equals(studentId)) {
                HostelRoom room = bed.getRoom();
                roomNo = room.getRoomNo();
                bedId = bed.getBedId();
                roomType = room.getType();
                roomDbId = room.getId();
                break;
            }
        }

        // Find all roommates (same room, but not this student)
        List<String> roommateNames = new ArrayList<>();
        if (roomDbId != null) {
            for (HostelBed bed : beds) {
                if (bed.getRoom() != null &&
                        bed.getRoom().getId().equals(roomDbId) &&
                        bed.getStudent() != null &&
                        !bed.getStudent().getId().equals(studentId)) {
                    roommateNames.add(bed.getStudent().getFullName());
                }
            }
        }

        // Local Guardian Info
        String localGuardian = student.getLocalGuardianName();
        String localGuardianAddress = student.getLocalGuardianAddress();

        Map<String, Object> res = new HashMap<>();
        res.put("studentId", student.getId());
        res.put("name", student.getFullName());
        res.put("branch", student.getBranch());
        res.put("year", student.getYearOfStudy());
        res.put("course", student.getCourse());
        res.put("mobile", student.getMobileNo());
        res.put("email", student.getEmailId());
        res.put("room", roomNo);
        res.put("bed", bedId);
        res.put("roomType", roomType);
        res.put("localGuardian", localGuardian);
        res.put("localGuardianAddress", localGuardianAddress);
        res.put("roommates", roommateNames);

        // Add ALL FIELDS from RegisterStudent
        res.put("dob", student.getDob());
        res.put("age", student.getAge());
        res.put("gender", student.getGender());
        res.put("religion", student.getReligion());
        res.put("category", student.getCategory());
        res.put("nationality", student.getNationality());
        res.put("aadharNo", student.getAadharNo());
        res.put("semesterYear", student.getSemesterYear());
        res.put("instituteName", student.getInstituteName());
        res.put("courseName", student.getCourseName());
        res.put("dateOfAdmission", student.getDateOfAdmission());
        res.put("hostelJoinDate", student.getHostelJoinDate());
        res.put("photoPath", student.getPhotoPath());
        res.put("fatherName", student.getFatherName());
        res.put("fatherOccupation", student.getFatherOccupation());
        res.put("fatherEducation", student.getFatherEducation());
        res.put("fatherEmail", student.getFatherEmail());
        res.put("fatherMobile", student.getFatherMobile());
        res.put("motherName", student.getMotherName());
        res.put("motherOccupation", student.getMotherOccupation());
        res.put("motherEducation", student.getMotherEducation());
        res.put("motherEmail", student.getMotherEmail());
        res.put("motherMobile", student.getMotherMobile());
        res.put("permanentAddress", student.getPermanentAddress());
        res.put("cityDistrict", student.getCityDistrict());
        res.put("state", student.getState());
        res.put("pinCode", student.getPinCode());
        res.put("phoneResidence", student.getPhoneResidence());
        res.put("phoneOffice", student.getPhoneOffice());
        res.put("officeAddress", student.getOfficeAddress());
        res.put("localGuardianPhone", student.getLocalGuardianPhone());
        res.put("localGuardianMobile", student.getLocalGuardianMobile());
        res.put("emergencyContactName", student.getEmergencyContactName());
        res.put("emergencyContactNo", student.getEmergencyContactNo());
        res.put("bloodGroup", student.getBloodGroup());
        res.put("seriousDisease", student.getSeriousDisease());
        res.put("regularMedication", student.getRegularMedication());
        res.put("hospitalRecord", student.getHospitalRecord());
        res.put("emergencyMedicine", student.getEmergencyMedicine());
        res.put("allergicTo", student.getAllergicTo());
        res.put("roomNo", roomNo); // for consistency

        return res;
    }

    // GET /api/student/filtered-list?gender=M
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

        // Prepare bed/room map for fast lookup
        List<HostelBed> allBeds = bedRepo.findAll();
        Map<Integer, String> studentIdToRoomNo = new HashMap<>();
        for (HostelBed bed : allBeds) {
            if (bed.getStudent() != null && bed.getRoom() != null) {
                studentIdToRoomNo.put(bed.getStudent().getId(), bed.getRoom().getRoomNo());
            }
        }

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
            // Add roomNo from map
            map.put("roomNo", studentIdToRoomNo.getOrDefault(student.getId(), ""));
            result.add(map);
        }
        return result;
    }
}