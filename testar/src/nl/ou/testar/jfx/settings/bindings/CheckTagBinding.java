package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.CheckBox;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class CheckTagBinding extends ConfigTagBinding<CheckBox, Boolean> {
    public CheckTagBinding(Settings settings, CheckBox control, Tag<Boolean> tag) {
        super(settings, control, tag);
    }

    @Override
    public void onBind() {
        control.setSelected(value);
        control.selectedProperty().addListener((observable, oldValue, newValue) -> {
            value = newValue;
        });
    }
}
