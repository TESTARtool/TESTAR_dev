package nl.ou.testar;


import java.util.Set;

public class ScreenshotWidgetJsonObject {
    Set<WidgetJsonObject> widgetJsonObjects;
    String screenshotPath;

    public ScreenshotWidgetJsonObject(Set<WidgetJsonObject> widgetJsonObjects, String screenshotPath) {
        this.widgetJsonObjects = widgetJsonObjects;
        this.screenshotPath = screenshotPath;
    }
}
