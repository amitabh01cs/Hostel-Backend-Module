package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    // basic CRUD from JpaRepository
}