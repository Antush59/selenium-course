package ru.example.lesson5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ElementsTest {

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

    private boolean waitText(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private static boolean firstPriceMin(List<WebElement> priceList) {
        List<Double> priceDoubleList = priceList.stream()
                .map(WebElement::getText)
                .map(s -> s.replace("$", ""))
                .map(Double::parseDouble)
                .toList();

        Double minPrice = priceDoubleList.stream()
                .min(Double::compareTo)
                .orElse(0.0);

        return Objects.equals(priceDoubleList.getFirst(), minPrice);
    }

    @Test
    void checkDropdownList() {
        login("standard_user", "secret_sauce");
        waitVisible(By.cssSelector("[data-test='secondary-header']"));
        Select firstSelect = new Select(driver.findElement(By.cssSelector("[data-test='product-sort-container']")));

        assertEquals(4, firstSelect.getOptions().size());

        firstSelect.selectByValue("lohi");
        wait.until(d -> firstPriceMin(d.findElements(By.cssSelector("[data-test='inventory-item-price']"))));
        List<WebElement> priceLoHiList = driver.findElements(By.cssSelector("[data-test='inventory-item-price']"));

        assertTrue(firstPriceMin(priceLoHiList));

        Select secondSelect = new Select(driver.findElement(By.cssSelector("[data-test='product-sort-container']")));
        secondSelect.selectByValue("za");
        List<WebElement> productNameZAList = driver.findElements(By.cssSelector("[data-test='inventory-item-name']"));

        assertEquals("https://www.saucedemo.com/inventory.html", driver.getCurrentUrl());
        assertEquals("Test.allTheThings() T-Shirt (Red)", productNameZAList.getFirst().getText());
    }

    @Test
    void testingCheckbox() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");

        By checkboxes = By.cssSelector("input[type='checkbox']");

        assertFalse(driver.findElements(checkboxes).get(0).isSelected());
        assertTrue(driver.findElements(checkboxes).get(1).isSelected());

        driver.findElements(checkboxes).get(0).click();
        assertTrue(driver.findElements(checkboxes).get(0).isSelected());

        driver.findElements(checkboxes).get(1).click();
        assertFalse(driver.findElements(checkboxes).get(1).isSelected());

        driver.findElements(checkboxes).get(0).click();
        assertFalse(driver.findElements(checkboxes).get(0).isSelected());

        driver.findElements(checkboxes).get(1).click();
        assertTrue(driver.findElements(checkboxes).get(1).isSelected());
    }

    @Test
    void JSAlerts() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");

        driver.findElement(By.cssSelector("[onclick='jsAlert()']")).click();
        Alert jsAlert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("I am a JS Alert", jsAlert.getText());
        jsAlert.accept();

        driver.findElement(By.cssSelector("[onclick='jsConfirm()']")).click();
        Alert jsConfirmDismiss = wait.until(ExpectedConditions.alertIsPresent());
        jsConfirmDismiss.dismiss();
        assertEquals("You clicked: Cancel", driver.findElement(By.id("result")).getText());

        driver.findElement(By.cssSelector("[onclick='jsConfirm()']")).click();
        Alert jsConfirmOk = wait.until(ExpectedConditions.alertIsPresent());
        jsConfirmOk.accept();
        assertEquals("You clicked: Ok", driver.findElement(By.id("result")).getText());

        driver.findElement(By.cssSelector("[onclick='jsPrompt()']")).click();
        Alert jsPrompt = wait.until(ExpectedConditions.alertIsPresent());
        jsPrompt.sendKeys("Hi, my name is Anton");
        jsPrompt.accept();
        assertEquals("You entered: Hi, my name is Anton", driver.findElement(By.id("result")).getText());
    }

    @Test
    void iframeExample() {
        driver.get("https://the-internet.herokuapp.com/iframe");
        driver.switchTo().frame(driver.findElement(By.id("mce_0_ifr")));

        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        assertEquals("Your content goes here.", body.getText());

        driver.switchTo().defaultContent();
        assertTrue(driver.findElement(By.tagName("h3")).isDisplayed());
    }

    @Test
    void windowsTesting() {
        driver.get("https://the-internet.herokuapp.com/windows");
        String main = driver.getWindowHandle();

        waitClickable(By.linkText("Click Here")).click();
        wait.until(d -> d.getWindowHandles().size() == 2);

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(main)) {
                driver.switchTo().window(handle);
            }
        }

        assertEquals("New Window", waitVisible(By.className("example")).getText());

        driver.close();
        driver.switchTo().window(main);

        assertEquals("Opening a new window",
                waitVisible(By.xpath("//h3[normalize-space()='Opening a new window']")).getText());
    }

    @Test
    void uploadFileTesting() throws IOException {
        driver.get("https://the-internet.herokuapp.com/upload");

        Path file = Files.createTempFile("test", ".txt");
        Files.writeString(file, "testing from test.txt");

        waitVisible(By.id("file-upload")).sendKeys(file.toAbsolutePath().toString());
        waitClickable(By.id("file-submit")).click();

        assertTrue(waitText(By.id("uploaded-files"),file.getFileName().toString()));
    }

    @Test
    void actionsTesting() {
        driver.get("https://the-internet.herokuapp.com/drag_and_drop");

        WebElement source = driver.findElement(By.id("column-a"));
        WebElement target = driver.findElement(By.id("column-b"));

        new Actions(driver).dragAndDrop(source, target).perform();
        waitText(By.id("column-a"), "B");

        source = driver.findElement(By.id("column-a"));
        target = driver.findElement(By.id("column-b"));

        assertEquals("A", target.getText());
        assertEquals("B", source.getText());
    }
}
