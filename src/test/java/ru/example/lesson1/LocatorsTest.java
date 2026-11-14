package ru.example.lesson1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocatorsTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new EdgeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void findsLoginForm() {
        driver.get("https://www.saucedemo.com/");

        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.cssSelector("input[data-test='password']"));
        WebElement loginButton = driver.findElement(By.cssSelector("input[data-test='login-button']"));

        assertTrue(username.isDisplayed());
        assertTrue(password.isDisplayed());
        assertTrue(loginButton.isDisplayed());

        System.out.println("placeholder: " + username.getAttribute("placeholder"));
        System.out.println("тег кнопки: " + loginButton.getTagName());
    }

    @Test
    void countInputs() {
        driver.get("https://www.saucedemo.com/");

        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        int sizeInputs = inputs.size();
        System.out.println("Найдено input: " + sizeInputs);

        assertFalse(inputs.isEmpty());
        assertEquals(3, sizeInputs, "На форме логина должно быть 3 поля input");
    }
}
