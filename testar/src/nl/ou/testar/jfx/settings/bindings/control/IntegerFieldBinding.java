package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.TextField;
import org.apache.commons.lang.math.NumberUtils;

public class IntegerFieldBinding extends FieldBinding<Integer> {

    public IntegerFieldBinding(TextField control) {
        super(control);
    }

    @Override
    protected Integer valueFromText(String text) {
        return NumberUtils.toInt(text, 0);
    }

    @Override
    public void onBind(Callback<Integer> callback) {
        super.onBind(callback);
        control.setPromptText("0");
    }
}
