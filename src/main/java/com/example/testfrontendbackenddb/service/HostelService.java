package com.example.testfrontendbackenddb.service;

import com.example.testfrontendbackenddb.repository.HostelAllocationRepository;
import com.example.testfrontendbackenddb.repository.HostelBedRepository;
import com.example.testfrontendbackenddb.repository.HostelRoomRepository;
import com.example.testfrontendbackenddb.repository.RegisterStudentRepository;

import com.example.testfrontendbackenddb.entity.HostelAllocation;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.entity.HostelRoom;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HostelService {

    @Autowired
    private HostelRoomRepository roomRepo;
    @Autowired
    private HostelBedRepository bedRepo;
    @Autowired
    private HostelAllocationRepository allocRepo;
    @Autowired
    private RegisterStudentRepository studentRepo;

    public List<HostelRoom> getAllRooms() {
        return roomRepo.findAll();
    }

    public List<HostelBed> getBedsByRoom(Integer roomId) {
        // Use findByRoomId if available
        return bedRepo.findByRoomId(roomId);
    }

    public List<HostelAllocation> getAllAllocations() {
        return allocRepo.findAll();
    }

    public HostelAllocation allocateBed(Integer studentId, Integer roomId, Integer bedId) {
        RegisterStudent student = studentRepo.findById(studentId).orElseThrow();
        HostelRoom room = roomRepo.findById(roomId).orElseThrow();
        HostelBed bed = bedRepo.findById(bedId).orElseThrow();

        if (!"empty".equals(bed.getStatus())) {
            throw new RuntimeException("Bed not available");
        }

        HostelAllocation alloc = new HostelAllocation();
        alloc.setStudent(student);
        alloc.setRoom(room);
        alloc.setBed(bed);
        alloc.setAllocatedOn(new Date());
        HostelAllocation saved = allocRepo.save(alloc);

        bed.setStatus("occupied");
        bed.setStudent(student);
        bedRepo.save(bed);

        return saved;
    }

    public void removeAllocation(Integer allocationId) {
        HostelAllocation alloc = allocRepo.findById(allocationId).orElseThrow();
        HostelBed bed = alloc.getBed();
        allocRepo.deleteById(allocationId);
        bed.setStatus("empty");
        bed.setStudent(null);
        bedRepo.save(bed);
    }

    public HostelAllocation relocateAllocation(Integer allocationId, Integer newRoomId, Integer newBedId) {
        HostelAllocation alloc = allocRepo.findById(allocationId).orElseThrow();
        HostelBed oldBed = alloc.getBed();
        HostelRoom newRoom = roomRepo.findById(newRoomId).orElseThrow();
        HostelBed newBed = bedRepo.findById(newBedId).orElseThrow();

        if (!"empty".equals(newBed.getStatus())) {
            throw new RuntimeException("New bed not available");
        }

        oldBed.setStatus("empty");
        oldBed.setStudent(null);
        bedRepo.save(oldBed);

        alloc.setRoom(newRoom);
        alloc.setBed(newBed);
        alloc.setAllocatedOn(new Date());
        allocRepo.save(alloc);

        newBed.setStatus("occupied");
        newBed.setStudent(alloc.getStudent());
        bedRepo.save(newBed);

        return alloc;
    }
}