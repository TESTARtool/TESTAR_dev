package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextField;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class StringFieldTagBinding extends FieldTagBinding<String> {

    public StringFieldTagBinding(Settings settings, TextField control, Tag<String> tag) {
        super(settings, control, tag);
    }

    @Override
    protected String valueFromText(String text) {
        return text;
    }
}
