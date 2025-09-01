package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.HostelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HostelRoomRepository extends JpaRepository<HostelRoom, Integer> {
    List<HostelRoom> findByHostelNameIgnoreCase(String hostelName);
}