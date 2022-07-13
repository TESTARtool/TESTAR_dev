package nl.ou.testar.jfx.utils;

import java.awt.DisplayMode;

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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DisplayModeWrapper)) {
            return false;
        }
        DisplayModeWrapper wrapper = (DisplayModeWrapper) obj;
        return (mode.getWidth() == wrapper.mode.getWidth() && mode.getHeight() == wrapper.mode.getHeight());
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
