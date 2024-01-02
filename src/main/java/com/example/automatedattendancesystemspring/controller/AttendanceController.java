package com.example.automatedattendancesystemspring.controller;

import com.example.automatedattendancesystemspring.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

@RestController
@RequestMapping("/api/mark")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;
    @GetMapping
    public String MarkAttendance(@RequestParam String email, @RequestParam String psswrd) throws InterruptedException {
        return attendanceService.markAttendance(email, psswrd);
    }
}
