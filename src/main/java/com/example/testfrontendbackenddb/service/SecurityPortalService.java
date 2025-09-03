package com.example.testfrontendbackenddb.service;

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
            map.put("name", s.getFullName());
            map.put("fullName", s.getFullName());
            map.put("course", s.getCourse());
            map.put("passNumber", passId);
            map.put("passType", pass.getPassType());
            map.put("reason", pass.getReason());
            map.put("destination", pass.getPlaceToVisit());
            map.put("status", status);
            map.put("checkOutTime", getActionTime(passLogs, "checkout"));
            map.put("checkInTime", getActionTime(passLogs, "checkin"));
            map.put("photoUrl", s.getPhotoPath());
            map.put("toTime", pass.getToTime()); // Add arrival time for frontend
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
            map.put("studentId", anyLog.getStudentId());
            map.put("name", anyLog.getStudentName());
            map.put("studentName", anyLog.getStudentName());
            map.put("course", student != null ? student.getCourse() : "");
            map.put("branch", student != null ? student.getBranch() : "");
            // 👇 This is the line that ensures passNumber is always sent
            map.put("passNumber", passId); // or optPass.map(GatePassRequest::getPassNumber).orElse(passId)
            map.put("passType", optPass.map(GatePassRequest::getPassType).orElse(""));
            map.put("reason", anyLog.getReason());
            map.put("destination", anyLog.getDestination());
            map.put("gender", student != null ? student.getGender() : "");
            map.put("checkOutTime", getActionTime(passLogs, "checkout"));
            map.put("checkInTime", getActionTime(passLogs, "checkin"));
            map.put("photoUrl", student != null ? student.getPhotoPath() : "");
            map.put("toTime", toTime);
            result.add(map);
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
                .map(l -> l.getTimestamp() != null ? l.getTimestamp().toString() : null)
                .findFirst().orElse(null);
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
