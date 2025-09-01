package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.GatePassRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface GatePassRequestRepository extends JpaRepository<GatePassRequest, Integer> {
    List<GatePassRequest> findByStudentId(Integer studentId);
    List<GatePassRequest> findByStatus(String status);
    List<GatePassRequest> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<GatePassRequest> findByStatusAndFromTimeBetween(String status, Date from, Date to);

    // --- Added for counts endpoint ---
    int countByStudentIdAndStatus(Integer studentId, String status);
    int countByStudentId(Integer studentId);
}