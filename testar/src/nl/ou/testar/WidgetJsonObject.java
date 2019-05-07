package nl.ou.testar;

public class WidgetJsonObject {
    boolean enabled;
    String role;
    boolean blocked;
    BoundingPoly boundingPoly;
    String className;
    String title;
    String desc;
    String name;
    String toolTipText;
    String valuePattern;

    public WidgetJsonObject(boolean enabled, String role, boolean blocked, BoundingPoly boundingPoly, String className, String title, String desc, String name, String toolTipText, String valuePattern) {
        this.enabled = enabled;
        this.role = role;
        this.blocked = blocked;
        this.boundingPoly = boundingPoly;
        this.className = className;
        this.title = title;
        this.desc = desc;
        this.name = name;
        this.toolTipText = toolTipText;
        this.valuePattern = valuePattern;
    }
}
