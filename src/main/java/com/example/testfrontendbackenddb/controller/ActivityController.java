package com.example.testfrontendbackenddb.‎controller;

import com.example.testfrontendbackenddb.entity.UserActivity;
import com.example.testfrontendbackenddb.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Adjust for security as needed
public class ActivityController {

    @Autowired
    private UserActivityRepository repository;

    // Endpoint to receive activity logs from frontend
    @PostMapping("/track")
    public void trackActivity(@RequestBody UserActivity activity) {
        if (activity.getTimestamp() == null) {
            activity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        repository.save(activity);
    }

    // Endpoint to fetch all activities of a specific user
    @GetMapping("/user-activities/{userId}")
    public List<UserActivity> getUserActivities(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }
}
