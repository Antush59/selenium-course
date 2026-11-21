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

public class LocatorsBooksTest {

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
    void findBooksCard() {
        driver.get("https://books.toscrape.com/");

        List<WebElement> booksCards = driver.findElements(By.cssSelector(".product_pod"));

        assertEquals(20, booksCards.size());
    }

    @Test
    void findTitleFirstBook() {
        driver.get("https://books.toscrape.com/");

        WebElement headerBook = driver.findElement(By.cssSelector(".product_pod h3 a"));


        String visibleText = headerBook.getText();
        System.out.println("Заголовок первой книги: " + visibleText);
        System.out.println("Полное название (атрибут title): " + headerBook.getAttribute("title"));

        assertFalse(visibleText.isEmpty(), "Заголовок книги не должен быть пустым");
    }

    @Test
    void findPriceFirstBook() {
        driver.get("https://books.toscrape.com/");

        String priceText = driver.findElement(By.xpath("//p[@class='price_color']")).getText();

        System.out.println("Цена первой книги: " + priceText);

        assertFalse(priceText.isEmpty(), "Цена не должна быть пустой");
        assertTrue(priceText.startsWith("£"), "Цена должна быть в фунтах");
    }

    @Test
    void countAllImg() {
        driver.get("https://books.toscrape.com/");

        List<WebElement> images = driver.findElements(By.cssSelector(".thumbnail"));

        System.out.println("Количество картинок на странице: " + images.size());

        assertEquals(20, images.size());
    }
}
