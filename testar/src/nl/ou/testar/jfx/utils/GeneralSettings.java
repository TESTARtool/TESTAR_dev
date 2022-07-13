package nl.ou.testar.jfx.utils;

import java.awt.DisplayMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralSettings {

    private String source;
    private String driver;
    private String location;
    private DisplayMode displayMode;

    private final static String TEMPLATE = "\"([^\"]*)\"\\s+\"(\\d+)x(\\d+)\\+(\\d+)\\+(\\d+)\"\\s+\"([^\"]*)\"";
    private final static String FORMAT = "\"%s\" \"%dx%d+%d+%d\" \"%s\"";

    public GeneralSettings(String source, DisplayMode fallbackDisplayMode) {
        this.source = source;
        Pattern pattern = Pattern.compile(TEMPLATE);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            driver = matcher.group(1);
            location = matcher.group(6);

            int width = Integer.parseInt(matcher.group(2));
            int height = Integer.parseInt(matcher.group(3));
            int bitDepth = Integer.parseInt(matcher.group(4));
            int refreshRate = Integer.parseInt(matcher.group(5));
            displayMode = new DisplayMode(width, height, bitDepth, refreshRate);
        }
        else {
            driver = "";
            location = "";
            displayMode = fallbackDisplayMode;
        }
    }

    public String getSource() {
        return source;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, driver, displayMode.getWidth(), displayMode.getHeight(),
                displayMode.getBitDepth(), displayMode.getRefreshRate(), location);
    }
}
