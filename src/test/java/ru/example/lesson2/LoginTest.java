package ru.example.lesson2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.get("https://www.saucedemo.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='username']")))
                .sendKeys(username);
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("[data-test='login-button']")).click();
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Test
    void successfulLoginOpensInventory() {
        login("standard_user", "secret_sauce");

        wait.until(ExpectedConditions.urlContains("inventory"));

        assertEquals("https://www.saucedemo.com/inventory.html", driver.getCurrentUrl());
        assertEquals(6, driver.findElements(By.cssSelector(".inventory_item")).size());

        assertTrue(driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).isDisplayed());
        assertTrue(driver.findElements(By.cssSelector("[data-test='shopping-cart-badge']")).isEmpty());
    }

    @Test
    void wrongPasswordShowsError() {
        login("standard_user", "wrong_password");

        WebElement error = waitVisible(By.cssSelector("[data-test='error']"));

        assertEquals("Epic sadface: Username and password do not match any user in this service", error.getText());
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }

    @Test
    void checkErrorMessageIfLoginAndPasswordIsNotProvided() {
        login("", "");

        WebElement error = waitVisible(By.cssSelector("[data-test='error']"));

        assertEquals("Epic sadface: Username is required", error.getText());
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }

    @Test
    void checkErrorMessageIfLoginIsNotProvider() {
        login("", "secret_sauce");

        WebElement error = waitVisible(By.cssSelector("[data-test='error']"));
        assertEquals("Epic sadface: Username is required", error.getText());
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }

    @Test
    void checkErrorMessageIfPasswordIsNotProvider() {
        login("standard_user", "");

        WebElement error = waitVisible(By.cssSelector("[data-test='error']"));
        assertEquals("Epic sadface: Password is required", error.getText());
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }

    @Test
    void addingItemShowsBadge() {
        login("standard_user", "secret_sauce");

        waitVisible(By.cssSelector(".inventory_item"));

        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();

        assertTrue(driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("[data-test='shopping-cart-badge']")).isDisplayed());

        WebElement badge = waitVisible(By.cssSelector("[data-test='shopping-cart-badge']"));
        assertEquals("1", badge.getText());
        assertEquals("Remove", driver.findElement(By.cssSelector("[data-test='remove-sauce-labs-backpack']")).getText());
    }

    @Test
    void checkLockedOutUser() {
        login("locked_out_user", "secret_sauce");
        String errorText = driver.findElement(By.cssSelector("[data-test='error']")).getText();

        assertTrue(errorText.contains("locked out"));
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }
}
