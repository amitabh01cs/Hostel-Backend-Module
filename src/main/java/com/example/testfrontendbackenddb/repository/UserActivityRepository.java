package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserId(String userId);
}
