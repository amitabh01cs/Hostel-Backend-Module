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

    // MODIFIED: This method now fetches all un-returned DAYS passes and today's HOURLY passes.
    public List<Map<String, Object>> getActivePassesWithTimes() {
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zoneId);
        ZonedDateTime startOfDayIST = today.atStartOfDay(zoneId);
        Date startOfDay = Date.from(startOfDayIST.toInstant());

        // 1. Fetch today's approved hourly passes
        List<GatePassRequest> hourlyPassesToday = gatePassRepo.findByStatusAndPassTypeAndFromTimeAfter("approved", "HOUR", startOfDay);

        // 2. Fetch ALL approved days passes
        List<GatePassRequest> allDaysPasses = gatePassRepo.findByStatusAndPassType("approved", "DAYS");
        
        // 3. Combine the lists
        List<GatePassRequest> combinedPasses = new ArrayList<>();
        combinedPasses.addAll(hourlyPassesToday);
        combinedPasses.addAll(allDaysPasses);
        
        // Remove duplicates just in case
        List<GatePassRequest> distinctPasses = combinedPasses.stream()
                .distinct()
                .collect(Collectors.toList());

        // 4. Fetch all logs to check their status efficiently
        List<Integer> passIds = distinctPasses.stream().map(GatePassRequest::getId).collect(Collectors.toList());
        List<SecurityPassActivityLog> relevantLogs = logRepo.findByGatePassIdIn(passIds);
        Map<Integer, List<SecurityPassActivityLog>> logsMap = relevantLogs.stream()
                .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (GatePassRequest pass : distinctPasses) {
            int passId = pass.getId();
            List<SecurityPassActivityLog> passLogs = logsMap.getOrDefault(passId, Collections.emptyList());
            String status = getPassStatus(passLogs);

            // If the pass is completed, we don't need to show it on the active dashboard.
            if ("completed".equals(status)) {
                continue;
            }

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
            map.put("toTime", pass.getToTime());

            if (s != null) {
                map.put("studentId", s.getId());
                String photoUrl = "";
                if (s.getPhotoPath() != null && !s.getPhotoPath().isEmpty()) {
                    photoUrl = s.getPhotoPath();
                } else if (s.getId() != null) {
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

    public List<Map<String, Object>> getCompletedLogsWithTimes(Date date, String gender) {
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate localDate = date.toInstant().atZone(zoneId).toLocalDate();
        ZonedDateTime startOfDayIST = localDate.atStartOfDay(zoneId);
        ZonedDateTime endOfDayIST = localDate.atTime(23, 59, 59, 999_000_000).atZone(zoneId);
        Date startOfDay = Date.from(startOfDayIST.toInstant());
        Date endOfDay = Date.from(endOfDayIST.toInstant());

        List<SecurityPassActivityLog> logs = logRepo.findByTimestampBetween(startOfDay, endOfDay);
        Map<Integer, List<SecurityPassActivityLog>> logsMap = logs.stream()
                .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<SecurityPassActivityLog>> entry : logsMap.entrySet()) {
            int passId = entry.getKey();
            List<SecurityPassActivityLog> passLogs = entry.getValue();
            String status = getPassStatus(passLogs);
            if (!"completed".equals(status)) continue;

            SecurityPassActivityLog anyLog = passLogs.get(0);
            Optional<RegisterStudent> optStudent = studentRepo.findById(anyLog.getStudentId());
            RegisterStudent student = optStudent.orElse(null);

            Optional<GatePassRequest> optPass = gatePassRepo.findById(passId);
            Date toTime = optPass.map(GatePassRequest::getToTime).orElse(null);

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

    public List<Map<String, Object>> getCurrentlyOutStudents(String gender) {
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zoneId);
        ZonedDateTime startOfDayIST = today.atStartOfDay(zoneId);
        ZonedDateTime endOfDayIST = today.atTime(23, 59, 59, 999_000_000).atZone(zoneId);
        Date startOfDay = Date.from(startOfDayIST.toInstant());
        Date endOfDay = Date.from(endOfDayIST.toInstant());

        List<SecurityPassActivityLog> logs = logRepo.findByTimestampBetween(startOfDay, endOfDay);
        Map<Integer, List<SecurityPassActivityLog>> logsMap = logs.stream()
                .collect(Collectors.groupingBy(SecurityPassActivityLog::getGatePassId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<SecurityPassActivityLog>> entry : logsMap.entrySet()) {
            List<SecurityPassActivityLog> passLogs = entry.getValue();
            String status = getPassStatus(passLogs);
            
            if ("out".equals(status)) {
                SecurityPassActivityLog anyLog = passLogs.get(0);
                Optional<RegisterStudent> optStudent = studentRepo.findById(anyLog.getStudentId());
                if (!optStudent.isPresent()) continue;
                
                RegisterStudent student = optStudent.get();

                if (gender != null && !gender.isEmpty() && student.getGender() != null) {
                    if (!gender.equalsIgnoreCase(student.getGender())) {
                        continue;
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("studentId", student.getId());
                map.put("fullName", student.getFullName());
                map.put("branch", student.getBranch());
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

    public void checkOut(Integer gatePassId) {
        GatePassRequest pass = gatePassRepo.findById(gatePassId).orElseThrow(() -> new RuntimeException("Pass not found"));
        List<SecurityPassActivityLog> logs = logRepo.findByGatePassId(gatePassId);
        boolean alreadyCheckedOut = logs.stream().anyMatch(l -> "checkout".equalsIgnoreCase(l.getAction()));
        if (alreadyCheckedOut) throw new RuntimeException("Already checked out");

        SecurityPassActivityLog log = new SecurityPassActivityLog();
        log.setGatePassId(gatePassId);
        log.setStudentId(pass.getStudent().getId());
        log.setStudentName(pass.getStudent().getFullName());
        log.setAction("checkout");

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        Instant nowIST = ZonedDateTime.now(zoneId).toInstant();
        log.setTimestamp(Date.from(nowIST));

        log.setReason(pass.getReason());
        log.setDestination(pass.getPlaceToVisit());
        logRepo.save(log);
    }

    public void checkIn(Integer gatePassId) {
        GatePassRequest pass = gatePassRepo.findById(gatePassId).orElseThrow(() -> new RuntimeException("Pass not found"));
        List<SecurityPassActivityLog> logs = logRepo.findByGatePassId(gatePassId);
        boolean alreadyCheckedIn = logs.stream().anyMatch(l -> "checkin".equalsIgnoreCase(l.getAction()));
        if (alreadyCheckedIn) throw new RuntimeException("Already checked in");

        SecurityPassActivityLog log = new SecurityPassActivityLog();
        log.setGatePassId(gatePassId);
        log.setStudentId(pass.getStudent().getId());
        log.setStudentName(pass.getStudent().getFullName());
        log.setAction("checkin");

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        Instant nowIST = ZonedDateTime.now(zoneId).toInstant();
        log.setTimestamp(Date.from(nowIST));

        log.setReason(pass.getReason());
        log.setDestination(pass.getPlaceToVisit());
        logRepo.save(log);
    }
}


