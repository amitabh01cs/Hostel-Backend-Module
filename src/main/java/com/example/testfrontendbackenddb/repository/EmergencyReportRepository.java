package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.EmergencyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyReportRepository extends JpaRepository<EmergencyReport, Integer> {
}