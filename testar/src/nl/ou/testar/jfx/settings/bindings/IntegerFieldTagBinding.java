package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextField;
import org.apache.commons.lang.math.NumberUtils;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class IntegerFieldTagBinding extends FieldTagBinding<Integer> {

    public IntegerFieldTagBinding(Settings settings, TextField control, Tag<Integer> tag) {
        super(settings, control, tag);
    }

    @Override
    protected String getInputPattern() {
        // Here we consider only non-negative decimal numbers
        return "\\d*";
    }

    @Override
    protected Integer valueFromText(String text) {
        return NumberUtils.toInt(text, 0);
    }

    @Override
    public void onBind() {
        super.onBind();
        control.setPromptText("0");
    }
}
