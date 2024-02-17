package com.example.automatedattendancesystemspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AutomatedAttendanceSystemSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatedAttendanceSystemSpringApplication.class, args);
    }

}
