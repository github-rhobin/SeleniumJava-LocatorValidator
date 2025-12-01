package validator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MainRunner {

	/**
     * Standalone entry point:
     * - Reads raw .java Page Object files from ./pageobjects
     * - Compiles them dynamically to .class in the same folder
     * - Loads each class via URLClassLoader
     * - Instantiates with WebDriver (expects constructor(WebDriver))
     * - Requires getTargetUrl() in each Page Object
     * - Validates all By fields and @FindBy annotations
     * - Writes a color-coded HTML report with locator values and execution results
     */
    public static void main(String[] args) {
        File poFolder = new File(System.getProperty("user.dir"), "pageobjects");
        if (!poFolder.exists()) {
            System.out.println("ERROR: 'pageobjects' folder not found at " + poFolder.getAbsolutePath());
            return;
        }

        // 1) Compile all .java files in folder
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("ERROR: No system Java compiler found. Use a JDK (not a JRE).");
            return;
        }

        File[] javaFiles = poFolder.listFiles((dir, name) -> name.endsWith(".java"));
        if (javaFiles != null) {
            for (File file : javaFiles) {
                System.out.println("Compiling: " + file.getName());
                int result = compiler.run(null, null, null, file.getPath());
                if (result != 0) {
                    System.out.println("Compilation FAILED for " + file.getName());
                } else {
                    System.out.println("Compilation OK: " + file.getName());
                }
            }
        }

        // 2) Setup headless WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox");
        WebDriver driver = new ChromeDriver(options);

        // 3) Load compiled classes and validate
        Map<String, Map<String, LocatorResult>> aggregateResults = new LinkedHashMap<>();

        try (URLClassLoader loader = URLClassLoader.newInstance(new URL[] { poFolder.toURI().toURL() })) {
            File[] classFiles = poFolder.listFiles((dir, name) -> name.endsWith(".class"));
            if (classFiles != null) {
                LocatorValidator validator = new LocatorValidator(driver);

                for (File file : classFiles) {
                    String className = file.getName().replace(".class", "");
                    System.out.println("Validating class: " + className);

                    try {
                        Class<?> clazz = Class.forName(className, true, loader);
                        Object pageObject = clazz.getConstructor(WebDriver.class).newInstance(driver);

                        // 🔑 Require getTargetUrl()
                        String url = (String) clazz.getMethod("getTargetUrl").invoke(pageObject);
                        driver.get(url);

                        Map<String, LocatorResult> results = validator.validate(pageObject);
                        aggregateResults.put(className + " (" + url + ")", results);

                        if (results.isEmpty()) {
                            System.out.println("  (No locators found)");
                        } else {
                            results.forEach((field, result) ->
                                System.out.println("  " + field + " -> " + result.status +
                                    " | " + result.locatorValue +
                                    " | Count=" + result.count));
                        }

                    } catch (NoSuchMethodException e) {
                        System.out.println("  ERROR: " + className + " must implement getTargetUrl().");
                    } catch (Throwable t) {
                        System.out.println("  ERROR validating " + className + ": " + safeMsg(t));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Loader error: " + safeMsg(e));
        } finally {
            driver.quit();
        }

        // 4) Write HTML report
        try {
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            File report = new File("locator-report_" + timestamp + ".html");
            new ReportWriter().writeHtml(report, aggregateResults, "(per PageObject)");
            System.out.println("\nReport written: " + report.getAbsolutePath());

            // Auto-open in default browser
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(report.toURI());
            }

        } catch (Exception e) {
            System.out.println("Failed to write report: " + safeMsg(e));
        }

        System.out.println("\nDone.");
    }

    private static String safeMsg(Throwable t) {
        String msg = t.getMessage();
        if (msg == null || msg.isBlank())
            return t.getClass().getSimpleName();
        int idx = msg.indexOf('\n');
        return idx > 0 ? msg.substring(0, idx).trim() : msg.trim();
    }
}
