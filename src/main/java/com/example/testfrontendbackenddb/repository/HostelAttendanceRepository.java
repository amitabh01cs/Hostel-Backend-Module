package com.example.testfrontendbackenddb.repository;

import com.example.testfrontendbackenddb.entity.HostelAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HostelAttendanceRepository extends JpaRepository<HostelAttendance, Integer> {
    List<HostelAttendance> findAllByAttendanceDate(Date attendanceDate);
    List<HostelAttendance> findAllByAttendanceDateBetween(Date from, Date to);
}