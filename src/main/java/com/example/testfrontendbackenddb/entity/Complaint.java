package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer studentId;
    private Date issueDate;
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status; // "New", "In Progress", "Resolved"
    private String photoUrl;
    private String feedback;
    private Boolean studentClosed = false;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

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