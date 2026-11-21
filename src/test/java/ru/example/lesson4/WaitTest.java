package ru.example.lesson4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class WaitTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new EdgeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.saucedemo.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='username']")))
                .sendKeys(username);
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void waitInvisibility(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    private void waitText(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    @Test
    void waitAfterClickErrorMessage() {
        login("", "secret_sauce");
        WebElement errorMessage = waitVisible(By.cssSelector("[data-test='error']"));

        assertEquals("Epic sadface: Username is required", errorMessage.getText());
    }

    @Test
    void errorDisappearsAfterClose() {
        login("", "secret_sauce");
        waitVisible(By.cssSelector("[data-test='error']"));
        waitClickable(By.cssSelector("[data-test='error-button']")).click();

        waitInvisibility(By.cssSelector("[data-test='error']"));
        assertTrue(driver.findElements(By.cssSelector("[data-test='error']")).isEmpty());
    }

    @Test
    void addToCart() {
        login("standard_user", "secret_sauce");
        waitClickable(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        waitText(By.cssSelector("[data-test='shopping-cart-badge']"), "1");

        assertEquals("1", driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText());
    }

    @Test
    void checkNavigation() {
        login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory"));

        assertTrue(waitVisible(By.cssSelector(".inventory_item")).isDisplayed());
    }

    @Test
    void waitTimesOutForMissingElement() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(1));

        TimeoutException exception = assertThrows(TimeoutException.class,
                () -> shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("no-such-element"))));

        System.out.println(exception.getMessage());
    }

    @Test
    void fluentWaitTest() {
        driver.get("https://www.saucedemo.com/");
        //WebDriverWait наследует FluentWait, поэтому pollingEvery доступен и у него. Разница не в возможностях,
        // а в удобных конструкторах
        Wait<WebDriver> waitFluent = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring((NoSuchElementException.class));
        driver.findElement(By.cssSelector("[data-test='username']")).sendKeys("standard_user");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
        WebElement result = waitFluent.until(driver -> {
            WebElement element = driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']"));
            if (element.isDisplayed()) {
                return element;
            } else {
                return null;
            }
        });
        result.click();

        assertEquals("1", driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).getText());
    }
}
