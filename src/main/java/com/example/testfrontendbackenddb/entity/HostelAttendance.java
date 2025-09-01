package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hostel_attendance")
public class HostelAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id")
    private Integer studentId;

    @Temporal(TemporalType.DATE)
    @Column(name = "attendance_date")
    private Date attendanceDate;

    @Column(name = "status")
    private String status; // "present" or "absent"

    @Column(name = "student_year")
    private String studentYear; // e.g. "I", "II", etc.

    @Column(name = "student_branch")
    private String studentBranch; // e.g. "CSE", "MECH", etc.

    @Column(name = "edited")
    private Boolean edited = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "edit_timestamp")
    private Date editTimestamp;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentYear() { return studentYear; }
    public void setStudentYear(String studentYear) { this.studentYear = studentYear; }

    public String getStudentBranch() { return studentBranch; }
    public void setStudentBranch(String studentBranch) { this.studentBranch = studentBranch; }

    public Boolean getEdited() { return edited; }
    public void setEdited(Boolean edited) { this.edited = edited; }

    public Date getEditTimestamp() { return editTimestamp; }
    public void setEditTimestamp(Date editTimestamp) { this.editTimestamp = editTimestamp; }
}