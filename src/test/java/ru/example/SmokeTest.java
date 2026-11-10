package ru.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmokeTest {

    private WebDriver driver;

//    @BeforeEach
//    void setUp() {
//        //создаем браузер. Selenium Manager сам найдет/скачает chromedriver.
//        driver = new ChromeDriver();
//    }

    @BeforeEach
    void setUp() {
        //создаем браузер. Selenium Manager сам найдет/скачает chromedriver.
        driver = new EdgeDriver();
    }

    @AfterEach
    void tearDown() {
        //Закрываем браузер и завершаем сессию, даже если тест упал
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void opensExampleCom() {
        driver.get("https://example.com/");

        String title = driver.getTitle();
        System.out.println("Заголовок страницы: " + title);

        assertEquals("Example Domain", title);
    }

    @Test
    void currentUrlIsExampleCom() {
        driver.get("https://example.com/");

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL: " + currentUrl);

        assertEquals("https://example.com/", currentUrl);
    }

    @Disabled("Урок 0: намеренно сломанный тест для чтения ошибки")
    @Test
    void opensExampleComCrash() {
        driver.get("Https://example.com/");

        String title = driver.getTitle();
        System.out.println("Заголовок страницы: " + title);

        assertEquals("Exampel Domain", title);
    }
}
