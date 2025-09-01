package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.AdminComplaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminComplaintRepository extends JpaRepository<AdminComplaint, Long> {
}