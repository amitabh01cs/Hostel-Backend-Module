package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.UserActivity;
import com.example.testfrontendbackenddb.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Adjust origins as per your security needs
public class ActivityController {

    @Autowired
    private UserActivityRepository repository;

    @PostMapping("/track")
    public void trackActivity(@RequestBody UserActivity activity) {
        if (activity.getTimestamp() == null) {
            activity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        repository.save(activity);
    }

    @GetMapping("/user-activities/{userId}")
    public List<UserActivity> getUserActivities(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }
}
