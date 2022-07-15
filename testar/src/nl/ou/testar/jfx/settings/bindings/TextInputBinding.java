package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextInputControl;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class TextInputBinding extends ConfigTagBinding<TextInputControl, String> {

    public TextInputBinding(Settings settings, TextInputControl control, Tag<String> tag) {
        super(settings, control, tag);
    }

    @Override
    public void onBind() {
        control.setText(value);
        control.textProperty().addListener((observable, oldValue, newValue) -> {
            value = newValue;
        });
    }
}
