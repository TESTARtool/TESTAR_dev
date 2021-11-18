package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.fruit.alayer.Tag;
import org.fruit.monkey.Settings;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public abstract class FieldTagBinding<T> extends ConfigTagBinding<TextField, T> {

    protected String getInputPattern() {
        return null;
    }

    abstract protected T valueFromText(String text);

    protected String valueToText(T value) {
        return value.toString();
    }

    public FieldTagBinding(Settings settings, TextField control, Tag<T> tag) {
        super(settings, control, tag);
    }

    @Override
    public void onBind() {
        control.textProperty().set(valueToText(value));
        control.textProperty().addListener((observable, oldTextValue, newTextValue) -> {
            value = valueFromText(newTextValue);
        });

        String patternString = getInputPattern();
        if (patternString != null) {
            Pattern pattern = Pattern.compile(patternString);
            TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                    pattern.matcher(change.getControlNewText()).matches() ? change : null);

            control.setTextFormatter(formatter);
        }
    }
}
