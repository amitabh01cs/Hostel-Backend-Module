package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.SecurityPassActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SecurityPassActivityLogRepository extends JpaRepository<SecurityPassActivityLog, Integer> {
    
    // Used in SecurityPortalService to efficiently fetch logs for multiple passes
    List<SecurityPassActivityLog> findByGatePassIdIn(List<Integer> gatePassIds);

    // Used to get logs within a specific date range (e.g., for today)
    List<SecurityPassActivityLog> findByTimestampBetween(Date start, Date end);

    // Used to get the most recent activity logs for the dashboard
    List<SecurityPassActivityLog> findTop10ByOrderByTimestampDesc();
    
    // You might need this for other parts of your application
    List<SecurityPassActivityLog> findByGatePassId(Integer gatePassId);
}

