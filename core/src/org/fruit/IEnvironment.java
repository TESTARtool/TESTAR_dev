package org.fruit;

public interface IEnvironment {
    /**
     * Get the display scale based on the windows handle.
     * @param windowHandle The handle of the window.
     * @return The scale of the display which shows the window, when the display could not be resolved 1.0 is returned.
     */
    double getDisplayScale(long windowHandle);
}
