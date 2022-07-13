package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextField;
import org.apache.commons.lang.math.NumberUtils;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class DoubleInputBinding extends FieldTagBinding<Double> {
    public DoubleInputBinding(Settings settings, TextField control, Tag<Double> tag) {
        super(settings, control, tag);
    }

    @Override
    protected String getInputPattern() {
        // Here we consider only non-negative fixed-point decimal numbers
        return "\\d*|\\d+\\,\\d*";
    }

    @Override
    protected Double valueFromText(String text) {
        return NumberUtils.toDouble(text, 0);
    }

    @Override
    public void onBind() {
        super.onBind();
        control.setPromptText("0");
    }
}
