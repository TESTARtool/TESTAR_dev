package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.matcher.MatcherResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

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
