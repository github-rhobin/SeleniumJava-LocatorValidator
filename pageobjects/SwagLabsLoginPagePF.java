import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SwagLabsLoginPagePF {

	private WebDriver driver;

    // 🔑 PageFactory locators
    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    // Required by validator
    public String getTargetUrl() {
        return "https://www.saucedemo.com/";
    }

    // Constructor with WebDriver + initElements
    public SwagLabsLoginPagePF(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Optional actions
    public void enterUsername(String text) {
        usernameInput.sendKeys(text);
    }

    public void enterPassword(String text) {
        passwordInput.sendKeys(text);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

}
