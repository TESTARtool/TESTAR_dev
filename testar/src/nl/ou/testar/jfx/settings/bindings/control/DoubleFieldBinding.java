package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.TextField;
import org.apache.commons.lang.math.NumberUtils;

public class DoubleFieldBinding extends FieldBinding<Double> {

    public DoubleFieldBinding(TextField control) {
        super(control);
    }

    @Override
    protected Double valueFromText(String text) {
        return NumberUtils.toDouble(text, 0);
    }

    @Override
    public void onBind(Callback<Double> callback) {
        super.onBind(callback);
        control.setPromptText("0");
    }
}
