package com.example.testfrontendbackenddb.service;

import java.text.SimpleDateFormat;
import com.example.testfrontendbackenddb.entity.GatePassRequest;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.entity.SecurityPassActivityLog;
import com.example.testfrontendbackenddb.repository.GatePassRequestRepository;
import com.example.testfrontendbackenddb.repository.SecurityPassActivityLogRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SecurityPortalService {

    private final GatePassRequestRepository gatePassRepo;
    private final SecurityPassActivityLogRepository logRepo;
    private final RegisterStudentRepository studentRepo;

    public SecurityPortalService(
            GatePassRequestRepository gatePassRepo,
            SecurityPassActivityLogRepository logRepo,
            RegisterStudentRepository studentRepo
    ) {
        this.gatePassRepo = gatePassRepo;
        this.logRepo = logRepo;
        this.studentRepo = studentRepo;
    }

    // Merged active passes (not completed), with checkOutTime and possibly checkInTime if partial
    public List<Map<String, Object>> getActivePassesWithTimes() {
        // Get today's date in IST
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zoneId);

        ZonedDateTime startOfDayIST = today.atStartOfDay(zoneId);
        ZonedDateTime endOfDayIST = today.atTime(23, 59, 59, 999_000_000).atZone(zoneId);

        Date startOfDay = Date.from(startOfDayIST.toInstant());
        Date endOfDay = Date.from(endOfDayIST.toInstant());

        List<GatePassRequest> passes = gatePassRepo.findByStatusAndFromTimeBetween("approved", startOfDay, endOfDay);

        // Get all logs for today
        List<SecurityPassActivityLog> logs = logRepo.findByTimestampBetween(startOfDay, endOfDay);

        // Group logs by gatePassId
        Map<Integer, List<SecurityPassActivityLog>> logsMap = logs.stream()
                .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (GatePassRequest pass : passes) {
            int passId = pass.getId();
            List<SecurityPassActivityLog> passLogs = logsMap.getOrDefault(passId, Collections.emptyList());
            String status = getPassStatus(passLogs);
            if ("completed".equals(status)) continue; // Only active passes

            Map<String, Object> map = new HashMap<>();
            RegisterStudent s = pass.getStudent();
            map.put("id", passId);
            map.put("passNumber", passId);
            map.put("name", s != null ? s.getFullName() : "");
            map.put("fullName", s != null ? s.getFullName() : "");
            map.put("course", s != null ? s.getCourse() : "");
            map.put("passType", pass.getPassType());
            map.put("reason", pass.getReason());
            map.put("destination", pass.getPlaceToVisit());
            map.put("status", status);
            map.put("checkOutTime", getActionTime(passLogs, "checkout"));
            map.put("checkInTime", getActionTime(passLogs, "checkin"));
            map.put("toTime", pass.getToTime()); // Add arrival time for frontend

            // --- IMPORTANT: Provide studentId and correct photoUrl (ALWAYS USE STUDENT ID, NOT PASS ID) ---
            if (s != null) {
                map.put("studentId", s.getId());
                String photoUrl = "";
                if (s.getPhotoPath() != null && !s.getPhotoPath().isEmpty()) {
                    photoUrl = s.getPhotoPath();
                } else if (s.getId() != null) {
                    // FIX: Always use student ID here, not pass ID
                    photoUrl = "https://hostel-backend-module-production-iist.up.railway.app/api/student/photo/" + s.getId();
                }
                map.put("photoUrl", photoUrl);
            } else {
                map.put("studentId", null);
                map.put("photoUrl", "");
            }
            result.add(map);
        }
        return result;
    }

    // Merged completed logs (completed passes), with checkOutTime and checkInTime
    public List<Map<String, Object>> getCompletedLogsWithTimes(Date date, String gender) {
        // 1. Get all logs for the date in IST
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate localDate = date.toInstant().atZone(zoneId).toLocalDate();

        ZonedDateTime startOfDayIST = localDate.atStartOfDay(zoneId);
        ZonedDateTime endOfDayIST = localDate.atTime(23, 59, 59, 999_000_000).atZone(zoneId);

        Date startOfDay = Date.from(startOfDayIST.toInstant());
        Date endOfDay = Date.from(endOfDayIST.toInstant());

        List<SecurityPassActivityLog> logs = logRepo.findByTimestampBetween(startOfDay, endOfDay);

        // 2. Group logs by gatePassId
        Map<Integer, List<SecurityPassActivityLog>> logsMap = logs.stream()
                .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<SecurityPassActivityLog>> entry : logsMap.entrySet()) {
            int passId = entry.getKey();
            List<SecurityPassActivityLog> passLogs = entry.getValue();
            String status = getPassStatus(passLogs);
            if (!"completed".equals(status)) continue;

            // Find any one log to get studentId, etc.
            SecurityPassActivityLog anyLog = passLogs.get(0);
            Optional<RegisterStudent> optStudent = studentRepo.findById(anyLog.getStudentId());
            RegisterStudent student = optStudent.orElse(null);

            // 🔥 Add this to get the GatePassRequest (for toTime and passNumber)
            Optional<GatePassRequest> optPass = gatePassRepo.findById(passId);
            Date toTime = optPass.map(GatePassRequest::getToTime).orElse(null);

            // Gender filtering
            if (gender != null && !gender.isEmpty() && student != null) {
                if (!gender.equalsIgnoreCase(student.getGender())) continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", passId);
            map.put("passNumber", passId);
            map.put("studentId", anyLog.getStudentId());
            map.put("name", anyLog.getStudentName());
            map.put("studentName", anyLog.getStudentName());
            map.put("course", student != null ? student.getCourse() : "");
            map.put("branch", student != null ? student.getBranch() : "");
            map.put("passType", optPass.map(GatePassRequest::getPassType).orElse(""));
            map.put("reason", anyLog.getReason());
            map.put("destination", anyLog.getDestination());
            map.put("gender", student != null ? student.getGender() : "");
            map.put("checkOutTime", getActionTime(passLogs, "checkout"));
            map.put("checkInTime", getActionTime(passLogs, "checkin"));
            map.put("toTime", toTime);

            // --- IMPORTANT: Provide robust photoUrl using student ID, not pass ID ---
            if (student != null) {
                String photoUrl = "";
                if (student.getPhotoPath() != null && !student.getPhotoPath().isEmpty()) {
                    photoUrl = student.getPhotoPath();
                } else if (student.getId() != null) {
                    photoUrl = "https://hostel-backend-module-production-iist.up.railway.app/api/student/photo/" + student.getId();
                }
                map.put("photoUrl", photoUrl);
            } else {
                map.put("photoUrl", "");
            }

            result.add(map);
        }
        return result;
    }

    // NEW METHOD to get currently out students
    public List<Map<String, Object>> getCurrentlyOutStudents(String gender) {
        // 1. Get today's date range in IST
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zoneId);
        ZonedDateTime startOfDayIST = today.atStartOfDay(zoneId);
        ZonedDateTime endOfDayIST = today.atTime(23, 59, 59, 999_000_000).atZone(zoneId);
        Date startOfDay = Date.from(startOfDayIST.toInstant());
        Date endOfDay = Date.from(endOfDayIST.toInstant());

        // 2. Fetch all of today's logs
        List<SecurityPassActivityLog> logs = logRepo.findByTimestampBetween(startOfDay, endOfDay);

        // 3. Group by pass ID
        Map<Integer, List<SecurityPassActivityLog>> logsMap = logs.stream()
            .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        // 4. Iterate and find students who are "out"
        for (Map.Entry<Integer, List<SecurityPassActivityLog>> entry : logsMap.entrySet()) {
            List<SecurityPassActivityLog> passLogs = entry.getValue();
            String status = getPassStatus(passLogs);
            
            if ("out".equals(status)) { // Check for "out" status specifically
                SecurityPassActivityLog anyLog = passLogs.get(0);
                Optional<RegisterStudent> optStudent = studentRepo.findById(anyLog.getStudentId());
                if (!optStudent.isPresent()) continue; // Skip if student not found
                
                RegisterStudent student = optStudent.get();

                // 5. Apply gender filter if provided
                if (gender != null && !gender.isEmpty() && student.getGender() != null) {
                    if (!gender.equalsIgnoreCase(student.getGender())) {
                        continue; // Skip if gender doesn't match
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("studentId", student.getId());
                map.put("fullName", student.getFullName());
                map.put("branch", student.getBranch());
                // Add any other details you need for the frontend
                result.add(map);
            }
        }
        return result;
    }


    private String getPassStatus(List<SecurityPassActivityLog> logs) {
        boolean checkedIn = logs.stream().anyMatch(l -> "checkin".equalsIgnoreCase(l.getAction()));
        boolean checkedOut = logs.stream().anyMatch(l -> "checkout".equalsIgnoreCase(l.getAction()));
        if (checkedIn) return "completed";
        if (checkedOut) return "out";
        return "active";
    }

    private String getActionTime(List<SecurityPassActivityLog> logs, String action) {
        return logs.stream()
            .filter(l -> action.equalsIgnoreCase(l.getAction()))
            .map(l -> {
                if (l.getTimestamp() == null) return null;
                // Format to IST
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                return sdf.format(l.getTimestamp());
            })
            .findFirst()
            .orElse(null);
    }

    public List<SecurityPassActivityLog> getRecentActivityLogs() {
        return logRepo.findTop10ByOrderByTimestampDesc();
    }

    // Always use IST time for check-out
    public void checkOut(Integer gatePassId) {
        GatePassRequest pass = gatePassRepo.findById(gatePassId).orElseThrow();
        List<SecurityPassActivityLog> logs = logRepo.findByGatePassId(gatePassId);
        boolean alreadyCheckedOut = logs.stream().anyMatch(l -> "checkout".equalsIgnoreCase(l.getAction()));
        if (alreadyCheckedOut) throw new RuntimeException("Already checked out");

        SecurityPassActivityLog log = new SecurityPassActivityLog();
        log.setGatePassId(gatePassId);
        log.setStudentId(pass.getStudent().getId());
        log.setStudentName(pass.getStudent().getFullName());
        log.setAction("checkout");

        // Set timestamp in IST
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        Instant nowIST = ZonedDateTime.now(zoneId).toInstant();
        log.setTimestamp(Date.from(nowIST));

        log.setReason(pass.getReason());
        log.setDestination(pass.getPlaceToVisit());
        logRepo.save(log);
    }

    // Always use IST time for check-in
    public void checkIn(Integer gatePassId) {
        GatePassRequest pass = gatePassRepo.findById(gatePassId).orElseThrow();
        List<SecurityPassActivityLog> logs = logRepo.findByGatePassId(gatePassId);
        boolean alreadyCheckedIn = logs.stream().anyMatch(l -> "checkin".equalsIgnoreCase(l.getAction()));
        if (alreadyCheckedIn) throw new RuntimeException("Already checked in");

        SecurityPassActivityLog log = new SecurityPassActivityLog();
        log.setGatePassId(gatePassId);
        log.setStudentId(pass.getStudent().getId());
        log.setStudentName(pass.getStudent().getFullName());
        log.setAction("checkin");

        // Set timestamp in IST
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        Instant nowIST = ZonedDateTime.now(zoneId).toInstant();
        log.setTimestamp(Date.from(nowIST));

        log.setReason(pass.getReason());
        log.setDestination(pass.getPlaceToVisit());
        logRepo.save(log);
    }
}
