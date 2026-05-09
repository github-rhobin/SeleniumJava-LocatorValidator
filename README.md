## Guidelines:

#### Step 1: Add your Page Object java files inside the "pageobjects" folder.

#### Step 2: Make sure each of your Page Object java files have this format:

```bash
import lines

public class <Class Name> {

WebDriver field
class constructor that accepts WebDriver object

	//declaration of the target Website URL
    public String getTargetUrl() {
    return "<Website URL>";
    }

Locators
Page Methods (optional)

}
```

#### Step 3: Double-click the batch-script "run-validator.bat" file to start the locator validation process.

#### Step 4: Check the HTML report for results.
