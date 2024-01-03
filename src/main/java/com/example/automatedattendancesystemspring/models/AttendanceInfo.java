package com.example.automatedattendancesystemspring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AttendanceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String subject;
    private Long attendancesMarked;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Override
    public String toString(){
        return "subject: " + subject + "\n" +
                "attendances marked: " + attendancesMarked +"\n"+
                "date: " + date.toString();
    }
}
