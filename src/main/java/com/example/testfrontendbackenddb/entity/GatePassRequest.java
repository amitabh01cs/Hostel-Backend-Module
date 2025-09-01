package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "gate_pass_request")
public class GatePassRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private RegisterStudent student;

    @Column(name = "pass_type")
    private String passType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_time")
    private Date fromTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_time")
    private Date toTime;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status")
    private String status; // pending/approved/rejected

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_at")
    private Date approvedAt;

    @Column(name = "place_to_visit")
    private String placeToVisit;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public RegisterStudent getStudent() { return student; }
    public void setStudent(RegisterStudent student) { this.student = student; }

    public String getPassType() { return passType; }
    public void setPassType(String passType) { this.passType = passType; }

    public Date getFromTime() { return fromTime; }
    public void setFromTime(Date fromTime) { this.fromTime = fromTime; }

    public Date getToTime() { return toTime; }
    public void setToTime(Date toTime) { this.toTime = toTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Date approvedAt) { this.approvedAt = approvedAt; }

    public String getPlaceToVisit() { return placeToVisit; }
    public void setPlaceToVisit(String placeToVisit) { this.placeToVisit = placeToVisit; }
}