package com.example.testfrontendbackenddb.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public LocalDateTime getActivityTime() { return activityTime; }
    public void setActivityTime(LocalDateTime activityTime) { this.activityTime = activityTime; }
}
