package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.GatePassRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface GatePassRequestRepository extends JpaRepository<GatePassRequest, Integer> {

    // Used in SecurityPortalService to get today's hourly passes
    List<GatePassRequest> findByStatusAndPassTypeAndFromTimeAfter(String status, String passType, Date fromTime);

    // Used in SecurityPortalService to get all un-returned days passes
    List<GatePassRequest> findByStatusAndPassType(String status, String passType);
    
    // You might need these for other parts of your application
    List<GatePassRequest> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<GatePassRequest> findByStatusAndFromTimeBetween(String status, Date from, Date to);
    int countByStudentIdAndStatus(Integer studentId, String status);
    int countByStudentId(Integer studentId);
}
