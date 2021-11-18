package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.TextField;

public class StringFieldBinding extends FieldBinding<String> {

    public StringFieldBinding(TextField control) {
        super(control);
    }

    @Override
    protected String valueFromText(String text) {
        return text;
    }
}
