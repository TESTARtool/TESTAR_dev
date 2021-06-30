package nl.ou.testar.visualvalidation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

public class DummyVisualValidator implements VisualValidationManager {
    @Override
    public void AnalyzeImage(State state, @Nullable AWTCanvas screenshot) {

    }

    @Override
    public void AnalyzeImage(State state, @Nullable AWTCanvas screenshot, @Nullable Widget widget) {

    }

    @Override
    public void Destroy() {

    }
}
