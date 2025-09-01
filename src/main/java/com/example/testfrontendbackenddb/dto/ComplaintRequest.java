package com.example.testfrontendbackenddb.dto;

import java.util.Date;

public class ComplaintRequest {
    private Integer studentId;
    private Date issueDate;
    private String topic;
    private String description;
    private String roomNo; // <-- Add this field

    // Getters/Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
}