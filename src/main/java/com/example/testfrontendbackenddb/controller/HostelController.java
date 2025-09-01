package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.dto.RoomTypeStats;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.entity.HostelRoom;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import com.example.testfrontendbackenddb.repository.HostelRoomRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class HostelController {

    private final HostelRoomRepository roomRepo;
    private final HostelBedRepository bedRepo;
    private final RegisterStudentRepository studentRepo;

    public HostelController(
            HostelRoomRepository roomRepo,
            HostelBedRepository bedRepo,
            RegisterStudentRepository studentRepo
    ) {
        this.roomRepo = roomRepo;
        this.bedRepo = bedRepo;
        this.studentRepo = studentRepo;
    }

    // Get all rooms for a specific hostel (with beds and student info)
    @GetMapping("/hostel/rooms")
    public List<HostelRoom> getRoomsByHostel(@RequestParam String hostelName) {
        return roomRepo.findByHostelNameIgnoreCase(hostelName);
    }

    // Assign student to a bed
    @PostMapping("/hostel/assign")
    public HostelBed assignStudentToBed(@RequestParam Integer bedId, @RequestParam Integer studentId) {
        HostelBed bed = bedRepo.findById(bedId).orElseThrow();
        RegisterStudent student = studentRepo.findById(studentId).orElseThrow();
        bed.setStudent(student);
        bed.setStatus("occupied");
        return bedRepo.save(bed);
    }

    // Remove student from bed
    @PostMapping("/hostel/remove-student")
    public HostelBed removeStudentFromBed(@RequestParam Integer bedId) {
        HostelBed bed = bedRepo.findById(bedId).orElseThrow();
        bed.setStudent(null);
        bed.setStatus("empty");
        return bedRepo.save(bed);
    }

    // Remove a bed from a room and update type
    @PostMapping("/hostel/remove-bed")
    public String removeBedFromRoom(@RequestParam Integer bedId) {
        HostelBed bed = bedRepo.findById(bedId).orElseThrow();
        HostelRoom room = bed.getRoom();

        // Remove the bed from the room's bed list
        room.getBeds().remove(bed);

        // Update room type based on new bed count
        int newCount = room.getBeds() != null ? room.getBeds().size() : 0;
        String newType;
        switch (newCount) {
            case 1: newType = "1-Seater"; break;
            case 2: newType = "2-Seater"; break;
            case 3: newType = "3-Seater"; break;
            default: newType = ""; // No beds left or error
        }
        room.setType(newType);

        // Save the room (JPA will delete the bed from DB due to orphanRemoval)
        roomRepo.save(room);

        return "Bed removed successfully";
    }

    // Get room type stats for dashboard
    @GetMapping("/stats/rooms")
    public List<RoomTypeStats> getRoomTypeStats() {
        List<HostelRoom> rooms = roomRepo.findAll();
        Map<String, RoomTypeStats> statsMap = new HashMap<>();
        for (HostelRoom room : rooms) {
            String type = room.getType();
            statsMap.putIfAbsent(type, new RoomTypeStats(type, 0, 0, 0));
            RoomTypeStats stat = statsMap.get(type);

            int roomBeds = room.getBeds() != null ? room.getBeds().size() : 0;
            int occupied = 0, available = 0;
            if (room.getBeds() != null) {
                for (HostelBed bed : room.getBeds()) {
                    if ("occupied".equals(bed.getStatus())) occupied++;
                    else available++;
                }
            }
            stat.setTotal(stat.getTotal() + roomBeds);
            stat.setOccupied(stat.getOccupied() + occupied);
            stat.setAvailable(stat.getAvailable() + available);
        }
        return new ArrayList<>(statsMap.values());
    }

    // Add bed, and auto-update type (1-Seater, 2-Seater, 3-Seater)
    @PostMapping("/hostel/add-bed")
    public HostelBed addBedToRoom(@RequestParam Integer roomId, @RequestParam(required = false) String adminType) {
        HostelRoom room = roomRepo.findById(roomId).orElseThrow();

        int bedCount = room.getBeds() != null ? room.getBeds().size() : 0;
        if (bedCount >= 3) throw new RuntimeException("Max 3 beds per room");

        // --- Update type before adding ---
        String newType;
        switch (bedCount + 1) { // bedCount is before adding, so +1 is the new total
            case 1: newType = "1-Seater"; break;
            case 2: newType = "2-Seater"; break;
            case 3: newType = "3-Seater"; break;
            default: newType = "3-Seater"; // fallback, should not happen due to check
        }
        room.setType(newType);
        roomRepo.save(room);

        char bedLetter = (char)('A' + bedCount);

        // BED ID LOGIC BASED ON ADMIN TYPE
        String bedIdPrefix = "BB"; // Default for Varahmihir
        if (adminType != null && adminType.equalsIgnoreCase("maitreyi")) {
            bedIdPrefix = "GB";
        }
        String bedId = bedIdPrefix + room.getRoomNo() + bedLetter;

        HostelBed newBed = new HostelBed();
        newBed.setBedId(bedId);
        newBed.setRoom(room);
        newBed.setStatus("empty");
        newBed.setStudent(null);

        // Add the new bed to the room's beds list
        room.getBeds().add(newBed);
        roomRepo.save(room); // Save room, beds will cascade

        return newBed;
    }

    // Get all registered students (for dropdown, etc.)
    @GetMapping("/students")
    public List<Map<String, Object>> getAllStudents() {
        List<RegisterStudent> students = studentRepo.findAll();
        return students.stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("fullName", s.getFullName());
            m.put("branch", s.getBranch());
            m.put("emailId", s.getEmailId());
            m.put("yearOfStudy", s.getYearOfStudy());
            m.put("instituteName", s.getInstituteName());
            m.put("course", s.getCourse());
            m.put("gender", s.getGender());
            m.put("category", s.getCategory());
            m.put("mobileNo", s.getMobileNo());
            m.put("regNo", s.getAadharNo());

            // Add father and mother details
            m.put("fatherName", s.getFatherName());
            m.put("fatherMobile", s.getFatherMobile());
            m.put("motherName", s.getMotherName());
            m.put("motherMobile", s.getMotherMobile());
            return m;
        }).collect(Collectors.toList());
    }

    // ========== STUDENT ROOM INFO ENDPOINT ==========
    @GetMapping("/student/room")
    public Map<String, Object> getStudentRoom(@RequestParam Integer studentId) {
        RegisterStudent student = studentRepo.findById(studentId).orElse(null);
        if (student == null) throw new RuntimeException("Student not found");

        List<HostelBed> beds = bedRepo.findAll();
        for (HostelBed bed : beds) {
            if (bed.getStudent() != null && bed.getStudent().getId().equals(studentId)) {
                HostelRoom room = bed.getRoom();
                Map<String, Object> res = new HashMap<>();
                res.put("roomNo", room.getRoomNo());
                res.put("bedId", bed.getBedId());
                res.put("roomType", room.getType());
                return res;
            }
        }
        return Collections.emptyMap(); // Not assigned
    }
}