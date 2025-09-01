package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "admin_complaints")
public class AdminComplaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer adminId;
    private String category;
    private String description;
    private String fileUrl;
    private Date createdAt;
    private String status; // e.g. New, In Progress, Resolved

    public AdminComplaint() {}

    public AdminComplaint(Integer adminId, String category, String description, String fileUrl, Date createdAt, String status) {
        this.adminId = adminId;
        this.category = category;
        this.description = description;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}