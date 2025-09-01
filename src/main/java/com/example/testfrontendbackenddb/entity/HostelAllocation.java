package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hostel_allocation")
public class HostelAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private RegisterStudent student;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private HostelRoom room;

    @ManyToOne
    @JoinColumn(name = "bed_id")
    private HostelBed bed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "allocated_on")
    private Date allocatedOn;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public RegisterStudent getStudent() { return student; }
    public void setStudent(RegisterStudent student) { this.student = student; }
    public HostelRoom getRoom() { return room; }
    public void setRoom(HostelRoom room) { this.room = room; }
    public HostelBed getBed() { return bed; }
    public void setBed(HostelBed bed) { this.bed = bed; }
    public Date getAllocatedOn() { return allocatedOn; }
    public void setAllocatedOn(Date allocatedOn) { this.allocatedOn = allocatedOn; }
}