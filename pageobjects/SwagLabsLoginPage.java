import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SwagLabsLoginPage {
    private WebDriver driver;

    private By usernameInput = By.id("user-name");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-button");

    public String getTargetUrl() {
    return "https://www.saucedemo.com/";
    }

    public SwagLabsLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterUsername(String text) {
        WebElement element = driver.findElement(usernameInput);
        element.sendKeys(text);
    }

    public void enterPassword(String text) {
        WebElement element = driver.findElement(passwordInput);
        element.sendKeys(text);
    }

    public void clickLoginButton() {
        WebElement element = driver.findElement(loginButton);
        element.click();
    }
}