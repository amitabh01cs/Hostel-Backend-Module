package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.HostelBed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HostelBedRepository extends JpaRepository<HostelBed, Integer> {
    List<HostelBed> findByRoomId(Integer roomId);
}