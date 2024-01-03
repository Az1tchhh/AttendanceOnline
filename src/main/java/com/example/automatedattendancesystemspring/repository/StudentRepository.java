package com.example.automatedattendancesystemspring.repository;

import com.example.automatedattendancesystemspring.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
    // You can define custom methods here if needed
}
