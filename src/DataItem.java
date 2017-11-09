
/**
 * A class to represent an item in an html form.
 */
public class DataItem {
    private String name;
    private String value;

    public DataItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DataItem(String name, int value) {
        this(name, value + "");
    }

    public DataItem(String name, long value) {
        this(name, value + "");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
