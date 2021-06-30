package nl.ou.testar.visualvalidation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

public interface VisualValidationManager {
    /**
     * Analyze the captured image and update the verdict.
     * @param state The state of the application.
     * @param screenshot The captured screenshot of the current state.
     */
    void AnalyzeImage(State state, @Nullable AWTCanvas screenshot);

    /**
     * Analyze the captured image and update the verdict.
     * @param state The state of the application.
     * @param screenshot The captured screenshot of the current state.
     * @param widget Optional, the corresponding widget when the screenshot is an action shot.
     */
    void AnalyzeImage(State state, @Nullable AWTCanvas screenshot, @Nullable Widget widget);

    /**
     * Destroy the visual validation manager.
     */
    void Destroy();
}
