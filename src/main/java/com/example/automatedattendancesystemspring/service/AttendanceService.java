package com.example.automatedattendancesystemspring.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class AttendanceService {
    public String markAttendance(String email, String password) throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        System.setProperty("webdriver.chrome.driver", "C:/Users/azama/Downloads/chromedriver_win32/chromedriver.exe");

//        driver.manage().window().maximize();
        try {
            driver.get("https://wsp.kbtu.kz");
            System.out.println(driver.getCurrentUrl());
            Thread.sleep(2000);

            WebElement buttonQuit = driver.findElement(By.xpath("//div[contains(@class, 'v-button')]//img[contains(@src, 'login_24.png')]"));
            buttonQuit.click();

            Thread.sleep(2000);
            System.out.println(driver.getCurrentUrl());
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
            System.out.println(driver.getCurrentUrl());
            WebElement buttonToAttendancePage = driver.findElement(By.xpath("//div[contains(@class, 'v-link')]//a[contains(@href, 'https://wsp.kbtu.kz/RegistrationOnline')]"));
            buttonToAttendancePage.click();
            System.out.println(driver.getCurrentUrl());
            Thread.sleep(2000);
            byte[] screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
            System.out.println(screenshot);
            driver.close();
            return Base64.getEncoder().encodeToString(screenshot);
        }catch (Exception e){
            driver.close();
            return "Something went wrong";
        }
    }
}
