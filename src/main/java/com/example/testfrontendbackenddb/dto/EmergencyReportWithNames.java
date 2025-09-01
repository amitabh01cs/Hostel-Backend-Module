package com.example.testfrontendbackenddb.dto;

import java.util.Date;

public class EmergencyReportWithNames {
//    public Integer id;
    public Integer adminId;
    public String adminName;
    public Integer studentId;
    public String studentName;
    public String transcript;
    public Date createdAt;
    public String status;

    public EmergencyReportWithNames() {}

    public EmergencyReportWithNames(Integer id, Integer adminId, String adminName, Integer studentId, String studentName, String transcript, Date createdAt, String status) {
//        this.id = id;
        this.adminId = adminId;
        this.adminName = adminName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.transcript = transcript;
        this.createdAt = createdAt;
        this.status = status;
    }

//    public Integer getId() { return id; }
//    public void setId(Integer id) { this.id = id; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}