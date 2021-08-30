package nl.ou.testar.jfx.utils;

import java.awt.*;

public class DisplayModeWrapper {

    private final DisplayMode mode;

    public DisplayModeWrapper(DisplayMode mode) {
        this.mode = mode;
    };

    public DisplayMode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return String.format("%dx%d", mode.getWidth(), mode.getHeight());
    }
}
