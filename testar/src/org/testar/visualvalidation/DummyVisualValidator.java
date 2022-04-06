package org.testar.visualvalidation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.visualvalidation.matcher.MatcherResult;

public class DummyVisualValidator implements VisualValidationManager {
    @Override
    public MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot) {
        return new MatcherResult();
    }

    @Override
    public MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot, @Nullable Widget widget) {
        return new MatcherResult();
    }

    @Override
    public void Destroy() {

    }
}
