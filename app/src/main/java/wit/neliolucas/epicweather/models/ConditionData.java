package wit.neliolucas.epicweather.models;

/**
 * Author Nelio Lucas
 * Date 11/1/2021
 */
public class ConditionData {

    private String icon;
    private String value;
    private String label;

    public ConditionData() {
    }

    public ConditionData(String icon, String value, String label) {
        this.icon = icon;
        this.value = value;
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
