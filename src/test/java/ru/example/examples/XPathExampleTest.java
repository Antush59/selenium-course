package ru.example.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XPathExampleTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        // Открываем локальный файл как обычную страницу: file:///C:/.../example.html
        Path page = Paths.get("src/test/resources/example.html").toAbsolutePath();
        driver.get(page.toUri().toString());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void xpathExamples() {
        // 1. По атрибуту: div, у которого data-id="101"
        WebElement notebook = driver.findElement(By.xpath("//div[@data-id='101']"));
        // ".//h2" — точка значит "искать внутри notebook", а не по всей странице
        System.out.println("1) товар: " + notebook.findElement(By.xpath(".//h2")).getText());

        // 2. По точному тексту, без учёта пробелов по краям
        WebElement submit = driver.findElement(By.xpath("//button[normalize-space()='Отправить']"));
        System.out.println("2) кнопка: " + submit.getText());

        // 3. contains по атрибуту + поиск всех совпадений
        List<WebElement> addButtons = driver.findElements(By.xpath("//button[contains(@class,'btn')]"));
        System.out.println("3) кнопок .btn: " + addButtons.size());

        // 4. Индекс: первая цена. Скобки обязательны, иначе это "div[1] внутри родителя"
        WebElement firstPrice = driver.findElement(By.xpath("(//p[@class='price'])[1]"));
        System.out.println("4) первая цена: " + firstPrice.getText());

        // 5. От подписи к полю: label "Email", затем следующий за ним input
        WebElement email = driver.findElement(
                By.xpath("//label[normalize-space()='Email']/following-sibling::input"));
        System.out.println("5) placeholder email: " + email.getAttribute("placeholder"));

        // 6. Вверх по дереву: нашли h2 "Мышь", поднялись к родительскому div
        WebElement mouseCard = driver.findElement(
                By.xpath("//h2[normalize-space()='Мышь']/parent::div"));
        System.out.println("6) карточка мыши, data-id=" + mouseCard.getAttribute("data-id"));

        // 7. Два условия одновременно: класс содержит msg И error
        List<WebElement> errors = driver.findElements(
                By.xpath("//p[contains(@class,'msg') and contains(@class,'error')]"));
        System.out.println("7) ошибок: " + errors.size() + " -> " + errors.get(0).getText());

        // 8. Проверка "атрибут существует": у кнопки есть disabled
        List<WebElement> disabled = driver.findElements(By.xpath("//button[@disabled]"));
        System.out.println("8) disabled-кнопок: " + disabled.size());

        assertEquals(3, addButtons.size());
        assertEquals("75000", firstPrice.getText());
        assertEquals("you@example.com", email.getAttribute("placeholder"));
        assertEquals("102", mouseCard.getAttribute("data-id"));
        assertEquals(1, errors.size());
        assertEquals(1, disabled.size());
    }
}
