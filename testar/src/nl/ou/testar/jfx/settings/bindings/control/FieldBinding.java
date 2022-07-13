package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public abstract class FieldBinding<T> extends AbstractControlBinding<TextField, T> {

    public FieldBinding(TextField control) {
        super(control);
    }

    protected String getInputPattern() {
        return null;
    }

    abstract protected T valueFromText(String text);

    protected String valueToText(T value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public void setValue(T value) {
        control.setText(valueToText(value));
    }

    @Override
    public T getValue() {
        return valueFromText(control.getText());
    }

    @Override
    public void onBind(Callback<T> callback) {
        control.textProperty().addListener((observable, oldValue, newValue) ->
                callback.onUpdate(valueFromText(newValue)));

        String patternString = getInputPattern();
        if (patternString != null) {
            Pattern pattern = Pattern.compile(patternString);
            TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                    pattern.matcher(change.getControlNewText()).matches() ? change : null);

            control.setTextFormatter(formatter);
        }
    }
}
