package com.example.automatedattendancesystemspring.service;

import com.example.automatedattendancesystemspring.models.AttendanceInfo;
import com.example.automatedattendancesystemspring.models.Student;
import com.example.automatedattendancesystemspring.repository.AttendanceInfoRepository;
import com.example.automatedattendancesystemspring.repository.StudentRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceInfoRepository attendanceInfoRepository;
    @Autowired
    private StudentRepository studentRepository;
    public String markAttendance(String email, String password) throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get("https://wsp.kbtu.kz");

            Thread.sleep(2000);
            WebElement buttonQuit = driver.findElement(By.xpath("//div[contains(@class, 'v-button')]//img[contains(@src, 'login_24.png')]"));
            buttonQuit.click();

            Thread.sleep(2000);
            WebElement email_area = driver.findElement(By.id("gwt-uid-4"));
            email_area.clear();
            email_area.sendKeys(email);

            Thread.sleep(1000);
            WebElement password_area = driver.findElement(By.id("gwt-uid-6"));
            password_area.clear();
            password_area.sendKeys(password);

            Thread.sleep(2000);
            password_area.sendKeys(Keys.ENTER);

            Thread.sleep(2000);
            WebElement buttonToMainPage = driver.findElement(By.xpath("//div[contains(@class, 'v-button')]//img[contains(@src, 'home.png')]"));
            buttonToMainPage.click();

            Thread.sleep(2000);
            WebElement buttonToAttendancePage = driver.findElement(By.xpath("//div[contains(@class, 'v-link')]//a[contains(@href, 'https://wsp.kbtu.kz/RegistrationOnline')]"));
            buttonToAttendancePage.click();

            Thread.sleep(2000);
            byte[] screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
            driver.close();


            Student currentStudent = saveStudent(email);
            saveAttendanceInfo(currentStudent, "subject");
            return Base64.getEncoder().encodeToString(screenshot);
        }catch (Exception e){
            driver.close();
            return "Something went wrong";
        }
    }
    @Transactional
    public Student getStudent(String email) {
        return studentRepository.findByEmail(email);
    }

    private Student saveStudent(String email){
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            student = new Student();
            student.setEmail(email);
            studentRepository.save(student);
        }
        return student;
    }
    private void saveAttendanceInfo(Student student, String subject){
        Date currentDate = new Date(); // Current date
        AttendanceInfo attendanceInfo = attendanceInfoRepository.findByStudentAndSubjectAndDate(student, subject, currentDate);

        if (attendanceInfo == null) {
            attendanceInfo = new AttendanceInfo();
            attendanceInfo.setStudent(student);
            attendanceInfo.setDate(currentDate);
            attendanceInfo.setSubject(subject);
            attendanceInfo.setAttendancesMarked(1L);
        } else {
            attendanceInfo.setAttendancesMarked(attendanceInfo.getAttendancesMarked() + 1);
        }
        attendanceInfoRepository.save(attendanceInfo);

    }
    public void cleanDB(){
        attendanceInfoRepository.deleteAll();
    }
}
