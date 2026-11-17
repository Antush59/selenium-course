package ru.example.lesson3;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XPathTest {

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

    @Test
    void findTravelCategory() {
        driver.get("https://books.toscrape.com/");
        WebElement travel = driver.findElement(By.xpath("//a[normalize-space()='Travel']"));
        assertEquals("Travel", travel.getText());
    }

    @Test
    void findBook() {
        driver.get("https://books.toscrape.com/");
        List<WebElement> books = driver.findElements(By.xpath("//a[@title='A Light in the Attic']"));
        assertTrue(books.getFirst().getText().contains("A Light in the"));
        assertEquals(1, books.size());
    }

    @Test
    void findBooksWithRatingEqualsTree() {
        driver.get("https://books.toscrape.com/");
        List<WebElement> books = driver.findElements(
                By.xpath("//p[contains(@class,'star-rating') and contains(@class,'Three')]"));

        assertEquals(3, books.size());
        assertTrue(books.getFirst().isDisplayed());
    }

    @Test
    void findPriceFirstBook() {
        driver.get("https://books.toscrape.com/");

        WebElement priceBook = driver.findElement(By.xpath("(//p[@class='price_color'])[1]"));

        System.out.println("Цена первой книги: " + priceBook.getText());
        assertEquals("£51.77", priceBook.getText());
        assertTrue(priceBook.isDisplayed());
    }

    @Test
    void divIndexDiffersFromDocumentIndex() {
        driver.get("https://books.toscrape.com/");

        List<WebElement> first = driver.findElements(By.xpath("//div[2]"));
        List<WebElement> second = driver.findElements(By.xpath("(//div)[2]"));

        System.out.println("Первый вывод: " + first.size());
        System.out.println("Второй вывод: " + second.size());
        System.out.println("Вывод второго: " + second.getFirst().getText());

        assertEquals(23, first.size());
        assertEquals(1, second.size());
    }

    @Test
    void checkErrorMessage() {
        driver.get("https://www.saucedemo.com/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@data-test='login-button']"))).click();
        WebElement errorWindow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[@data-test='error']")));

        assertTrue(errorWindow.isDisplayed());
        assertEquals("Epic sadface: Username is required", errorWindow.getText());
        assertEquals("https://www.saucedemo.com/", driver.getCurrentUrl());
    }

    @Test
    void closeError() {
        driver.get("https://www.saucedemo.com/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@data-test='login-button']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[@aria-label='Dismiss error']"))).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//h3[@data-test='error']")));

        assertTrue(driver.findElements(By.xpath("//h3[@data-test='error']")).isEmpty());
    }

    @Test
    void findPasswordByPlaceholder() {
        driver.get("https://www.saucedemo.com/");

        List<WebElement> elements = driver.findElements(By.xpath("//input[@placeholder='Password']"));

        assertEquals(1, elements.size());
        assertEquals("", elements.getFirst().getAttribute("value"));
        assertEquals("password", elements.getFirst().getAttribute(("data-test")));
    }
}
