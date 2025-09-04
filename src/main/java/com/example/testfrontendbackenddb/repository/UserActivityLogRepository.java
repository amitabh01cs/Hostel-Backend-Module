package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    // No extra code needed
}
