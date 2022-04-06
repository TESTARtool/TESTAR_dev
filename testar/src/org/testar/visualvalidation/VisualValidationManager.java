package org.testar.visualvalidation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.visualvalidation.matcher.MatcherResult;

public interface VisualValidationManager {
    /**
     * Analyze the captured image and update the verdict.
     *
     * @param state      The state of the application.
     * @param screenshot The captured screenshot of the current state.
     * @return The matching result of the expected text and the detected text on the captured image.
     */
    MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot);

    /**
     * Analyze the captured image and update the verdict.
     *
     * @param state      The state of the application.
     * @param screenshot The captured screenshot of the current state.
     * @param widget     Optional, the corresponding widget when the screenshot is an action shot.
     * @return The matching result of the expected text and the detected text on the captured image.
     */
    MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot, @Nullable Widget widget);

    /**
     * Destroy the visual validation manager.
     */
    void Destroy();
}
