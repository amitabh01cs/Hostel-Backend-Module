package com.example.testfrontendbackenddb.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hostel_room")
public class HostelRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_no")
    private String roomNo;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "type")
    private String type;

    @Column(name = "hostel_name")
    private String hostelName;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<HostelBed> beds;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
    public List<HostelBed> getBeds() { return beds; }
    public void setBeds(List<HostelBed> beds) { this.beds = beds; }
}