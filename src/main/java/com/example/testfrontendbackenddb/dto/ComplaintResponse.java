package com.example.testfrontendbackenddb.dto;

import java.util.Date;

public class ComplaintResponse {
    private Long id;
    private Integer studentId;
    private String fullName;
    private String mobileNo;
    private String emailId;
    private String roomNo; // <-- Add this line
    private Date issueDate;
    private String topic;
    private String description;
    private String status;
    private String photoUrl;
    private String feedback;
    private Boolean studentClosed;

    // Add roomNo to constructor
    public ComplaintResponse(Long id, Integer studentId, String fullName, String mobileNo, String emailId, String roomNo, Date issueDate, String topic, String description, String status, String photoUrl, String feedback, Boolean studentClosed) {
        this.id = id;
        this.studentId = studentId;
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.emailId = emailId;
        this.roomNo = roomNo;
        this.issueDate = issueDate;
        this.topic = topic;
        this.description = description;
        this.status = status;
        this.photoUrl = photoUrl;
        this.feedback = feedback;
        this.studentClosed = studentClosed;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Boolean getStudentClosed() { return studentClosed; }
    public void setStudentClosed(Boolean studentClosed) { this.studentClosed = studentClosed; }
}