package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.TextArea;
import nl.ou.testar.jfx.settings.bindings.AbstractConfigBinding;

public class TextAreaBinding extends AbstractControlBinding<TextArea, String> {

    public TextAreaBinding(TextArea control) {
        super(control);
    }

    @Override
    public void setValue(String value) {
        control.setText(value);
    }

    @Override
    public String getValue() {
        return control.getText();
    }

    @Override
    public void onBind(Callback<String> callback) {
        control.textProperty().addListener((observable, oldValue, newValue) -> {
            callback.onUpdate(newValue);
        });
    }
}
