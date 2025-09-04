package com.example.testfrontendbackenddb.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_activity_log")
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String userEmail;
    private String userType;
    private String ipAddress;
    private String actionType;
    private String pageUrl;
    private String actionDescription;
    private LocalDateTime activityTime;
}
