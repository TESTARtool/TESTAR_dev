package nl.ou.testar;


public class WidgetJsonObject {
    boolean enabled;
    String role;
    boolean blocked;
    double upperLeftX;
    double upperLeftY;
    double width;
    double height;
    String className;
    String title;
    String desc;
    String name;
    String toolTipText;
    String valuePattern;

    public WidgetJsonObject(boolean enabled, String role, boolean blocked, double upperLeftX, double upperLeftY, double width, double height, String className, String title, String desc, String name, String toolTipText, String valuePattern) {
        this.enabled = enabled;
        this.role = role;
        this.blocked = blocked;
        this.upperLeftX = upperLeftX;
        this.upperLeftY = upperLeftY;
        this.width = width;
        this.height = height;
        this.className = className;
        this.title = title;
        this.desc = desc;
        this.name = name;
        this.toolTipText = toolTipText;
        this.valuePattern = valuePattern;
    }
}
