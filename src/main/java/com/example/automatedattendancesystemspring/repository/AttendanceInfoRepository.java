package com.example.automatedattendancesystemspring.repository;

import com.example.automatedattendancesystemspring.models.AttendanceInfo;
import com.example.automatedattendancesystemspring.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AttendanceInfoRepository extends JpaRepository<AttendanceInfo, Long> {
    AttendanceInfo findByStudentAndSubjectAndDate(Student student, String subject, Date currentDate);
    // Custom methods can be added here
}
