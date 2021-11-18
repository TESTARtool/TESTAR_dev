package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextField;
import org.fruit.alayer.Tag;
import org.fruit.monkey.Settings;

public class StringFieldTagBinding extends FieldTagBinding<String> {

    public StringFieldTagBinding(Settings settings, TextField control, Tag<String> tag) {
        super(settings, control, tag);
    }

    @Override
    protected String valueFromText(String text) {
        return text;
    }
}
