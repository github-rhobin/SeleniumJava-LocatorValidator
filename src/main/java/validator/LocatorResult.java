package validator;

public class LocatorResult {

	public final String fieldName;
    public final String locatorType;
    public final String locatorValue;
    public final String status;
    public final int count;

    public LocatorResult(String fieldName, String locatorType, String locatorValue, String status, int count) {
        this.fieldName = fieldName;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
        this.status = status;
        this.count = count;
    }

}
