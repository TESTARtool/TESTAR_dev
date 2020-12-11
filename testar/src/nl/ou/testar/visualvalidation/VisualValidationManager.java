package nl.ou.testar.visualvalidation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;

public interface VisualValidationManager {
    /**
     * Analyze the captured image and update the verdict.
     * @param state The state of the application.
     * @param screenshot The captured screenshot of the current state.
     */
    void AnalyzeImage(State state, @Nullable AWTCanvas screenshot);

    /**
     * Destroy the visual validation manager.
     */
    void Destroy();
}
