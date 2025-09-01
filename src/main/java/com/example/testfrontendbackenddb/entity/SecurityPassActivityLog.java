package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "security_pass_activity_log")
public class SecurityPassActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer gatePassId;
    private Integer studentId;
    private String studentName;
    private String action; // "checkout", "checkin"
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String reason;
    private String destination;

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGatePassId() {
        return gatePassId;
    }
    public void setGatePassId(Integer gatePassId) {
        this.gatePassId = gatePassId;
    }

    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
}