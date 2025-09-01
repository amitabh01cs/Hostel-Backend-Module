package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
}