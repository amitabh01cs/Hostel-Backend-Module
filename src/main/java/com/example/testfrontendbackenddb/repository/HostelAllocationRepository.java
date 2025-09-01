package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.HostelAllocation;
import com.example.testfrontendbackenddb.entity.HostelBed;
import com.example.testfrontendbackenddb.entity.HostelRoom;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HostelAllocationRepository extends JpaRepository<HostelAllocation, Integer> {
    List<HostelAllocation> findByStudent(RegisterStudent student);
    List<HostelAllocation> findByRoom(HostelRoom room);
    List<HostelAllocation> findByBed(HostelBed bed);
}