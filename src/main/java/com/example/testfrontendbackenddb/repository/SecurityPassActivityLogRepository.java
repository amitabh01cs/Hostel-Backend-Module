package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.SecurityPassActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SecurityPassActivityLogRepository extends JpaRepository<SecurityPassActivityLog, Integer> {
    List<SecurityPassActivityLog> findTop10ByOrderByTimestampDesc();
    List<SecurityPassActivityLog> findByGatePassId(Integer gatePassId);
    List<SecurityPassActivityLog> findByTimestampBetween(Date start, Date end); // <--- Added!
}