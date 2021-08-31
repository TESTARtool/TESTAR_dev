package nl.ou.testar.jfx.utils;

import java.awt.*;

public class DisplayModeWrapper {

    private final DisplayMode mode;
    private final boolean supported;

    public DisplayModeWrapper(DisplayMode mode, boolean supported) {
        this.mode = mode;
        this.supported = supported;
    };

    public DisplayMode getMode() {
        return mode;
    }

    public boolean isSupported() {
        return supported;
    }

    @Override
    public String toString() {
        String result = String.format("%dx%d", mode.getWidth(), mode.getHeight());
        if (!supported) {
            result = result.concat(" (not supported)");
        }
        return result;
    }
}
