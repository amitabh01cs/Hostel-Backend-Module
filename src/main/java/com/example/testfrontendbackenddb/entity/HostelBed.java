package com.example.testfrontendbackenddb.entity;

import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "hostel_bed")
public class HostelBed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bed_id")
    private String bedId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonBackReference
    private HostelRoom room;

    @Column(name = "status")
    private String status; // "empty" or "occupied"

    @ManyToOne
    @JoinColumn(name = "student_id")
    private RegisterStudent student; // can be null

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getBedId() { return bedId; }
    public void setBedId(String bedId) { this.bedId = bedId; }
    public HostelRoom getRoom() { return room; }
    public void setRoom(HostelRoom room) { this.room = room; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public RegisterStudent getStudent() { return student; }
    public void setStudent(RegisterStudent student) { this.student = student; }
}