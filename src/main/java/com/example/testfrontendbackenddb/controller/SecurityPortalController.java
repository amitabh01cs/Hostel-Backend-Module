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

    // GET /api/security/active-passes
    @GetMapping("/active-passes")
    public List<Map<String, Object>> getActivePasses() {
        // Return merged details per pass (with both checkOutTime and checkInTime if available)
        return service.getActivePassesWithTimes();
    }

    // GET /api/security/completed-logs?date=yyyy-MM-dd&gender=M|F
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
                parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (Exception e) {
                parsedDate = new Date();
            }
        }
        // Return merged details per pass (with both checkOutTime and checkInTime)
        List<Map<String, Object>> logs = service.getCompletedLogsWithTimes(parsedDate, gender);
        return logs != null ? logs : new ArrayList<>();
    }

    // GET /api/security/activity-logs
    @GetMapping("/activity-logs")
    public List<SecurityPassActivityLog> getRecentActivityLogs() {
        return service.getRecentActivityLogs();
    }

    // POST /api/security/pass/{gatePassId}/checkout
    @PostMapping("/pass/{gatePassId}/checkout")
    public void checkOut(@PathVariable Integer gatePassId) {
        service.checkOut(gatePassId);
    }

    // POST /api/security/pass/{gatePassId}/checkin
    @PostMapping("/pass/{gatePassId}/checkin")
    public void checkIn(@PathVariable Integer gatePassId) {
        service.checkIn(gatePassId);
    }
}