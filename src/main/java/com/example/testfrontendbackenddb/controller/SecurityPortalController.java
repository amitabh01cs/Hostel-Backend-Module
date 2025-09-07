package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.service.SecurityPortalService;
import com.example.testfrontendbackenddb.entity.SecurityPassActivityLog;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/security")
public class SecurityPortalController {

    private final SecurityPortalService service;

    public SecurityPortalController(SecurityPortalService service) {
        this.service = service;
    }

    // This endpoint now uses the updated service logic to show all relevant active passes
    @GetMapping("/active-passes")
    public List<Map<String, Object>> getActivePasses() {
        return service.getActivePassesWithTimes();
    }

    @GetMapping("/completed-logs")
    public List<Map<String, Object>> getCompletedLogs(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String gender
    ) {
        Date parsedDate;
        if (date == null || date.isEmpty()) {
            parsedDate = new Date();
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                parsedDate = sdf.parse(date);
            } catch (Exception e) {
                parsedDate = new Date();
            }
        }
        List<Map<String, Object>> logs = service.getCompletedLogsWithTimes(parsedDate, gender);
        return logs != null ? logs : new ArrayList<>();
    }
    
    @GetMapping("/currently-out")
    public List<Map<String, Object>> getCurrentlyOutStudents(
        @RequestParam(required = false) String gender
    ) {
        return service.getCurrentlyOutStudents(gender);
    }

    @GetMapping("/activity-logs")
    public List<SecurityPassActivityLog> getRecentActivityLogs() {
        return service.getRecentActivityLogs();
    }

    @PostMapping("/pass/{gatePassId}/checkout")
    public void checkOut(@PathVariable Integer gatePassId) {
        service.checkOut(gatePassId);
    }

    @PostMapping("/pass/{gatePassId}/checkin")
    public void checkIn(@PathVariable Integer gatePassId) {
        service.checkIn(gatePassId);
    }
}

