package validator;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LocatorValidator {

	/**
	 * Validate all By fields in the given Page Object instance. Returns a Map of
	 * fieldName -> status message.
	 */

	private final WebDriver driver;

    public LocatorValidator(WebDriver driver) {
        this.driver = driver;
    }

    public Map<String, LocatorResult> validate(Object pageObject) {
        Map<String, LocatorResult> results = new LinkedHashMap<>();
        Class<?> clazz = pageObject.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                // ✅ Case 1: By locators
                if (field.getType().equals(By.class)) {
                    By locator = (By) field.get(pageObject);
                    results.put(field.getName(), validateLocator(field.getName(), "By", locator));
                }

                // ✅ Case 2: PageFactory @FindBy WebElements
                else if (field.getType().equals(WebElement.class) && field.isAnnotationPresent(FindBy.class)) {
                    FindBy annotation = field.getAnnotation(FindBy.class);
                    By locator = buildByFromFindBy(annotation);
                    if (locator != null) {
                        results.put(field.getName(), validateLocator(field.getName(), "@FindBy", locator));
                    } else {
                        results.put(field.getName(),
                            new LocatorResult(field.getName(), "@FindBy", "(unsupported)", "⚠️ Unsupported attributes", 0));
                    }
                }

            } catch (Exception e) {
                results.put(field.getName(),
                    new LocatorResult(field.getName(), "Unknown", "(error)", "ERROR: " + safeMsg(e), 0));
            }
        }

        return results;
    }

    private LocatorResult validateLocator(String fieldName, String type, By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            int count = elements.size();
            String status;

            if (count == 1) {
                status = "✅ Valid";
            } else if (count > 1) {
                status = "⚠️ Multiple matches";
            } else {
                status = "❌ Not found";
            }

            return new LocatorResult(fieldName, type, locator.toString(), status, count);
        } catch (Exception e) {
            return new LocatorResult(fieldName, type, locator.toString(), "❌ Error: " + safeMsg(e), 0);
        }
    }

    private By buildByFromFindBy(FindBy annotation) {
        if (!annotation.id().isEmpty()) return By.id(annotation.id());
        if (!annotation.name().isEmpty()) return By.name(annotation.name());
        if (!annotation.css().isEmpty()) return By.cssSelector(annotation.css());
        if (!annotation.xpath().isEmpty()) return By.xpath(annotation.xpath());
        if (!annotation.className().isEmpty()) return By.className(annotation.className());
        if (!annotation.linkText().isEmpty()) return By.linkText(annotation.linkText());
        if (!annotation.partialLinkText().isEmpty()) return By.partialLinkText(annotation.partialLinkText());
        return null;
    }

    private String safeMsg(Throwable t) {
        String msg = t.getMessage();
        if (msg == null || msg.isBlank()) return t.getClass().getSimpleName();
        int idx = msg.indexOf('\n');
        return idx > 0 ? msg.substring(0, idx).trim() : msg.trim();
    }


}
