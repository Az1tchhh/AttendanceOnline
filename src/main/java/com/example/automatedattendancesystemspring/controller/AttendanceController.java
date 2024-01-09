package com.example.automatedattendancesystemspring.controller;

import com.example.automatedattendancesystemspring.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Objects;

@RestController
@RequestMapping("/api/mark")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;
    @GetMapping
    public String MarkAttendance(@RequestParam String email, @RequestParam String psswrd) throws InterruptedException {
        return attendanceService.markAttendance(email, psswrd);
    }

    @PostMapping
    public String CleanDB(@RequestParam String password){
        if(Objects.equals(password, "Mazayka2003")){ //I know it's stupid, anyway local project
            attendanceService.cleanDB();
            return "cleaned";
        }
        return "YOU BASTARD";
    }
}
