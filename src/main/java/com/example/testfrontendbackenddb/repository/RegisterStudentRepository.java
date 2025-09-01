package com.example.testfrontendbackenddb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.testfrontendbackenddb.entity.RegisterStudent;
import java.util.List;

public interface RegisterStudentRepository extends JpaRepository<RegisterStudent, Integer> {
    List<RegisterStudent> findByGenderIgnoreCase(String gender);

}