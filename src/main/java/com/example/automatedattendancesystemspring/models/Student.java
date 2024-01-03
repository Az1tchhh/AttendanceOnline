package com.example.automatedattendancesystemspring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String email;
    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private List<AttendanceInfo> attendanceInfo;

    @Override
    public String toString(){
        return email + "\n" + attendanceInfo;
    }
}
