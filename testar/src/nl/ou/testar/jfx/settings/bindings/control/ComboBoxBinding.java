package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.ComboBox;

public class ComboBoxBinding<T> extends AbstractControlBinding<ComboBox<T>, T> {

    public ComboBoxBinding(ComboBox<T> control) {
        super(control);
    }

    @Override
    public void setValue(T value) {
        int index = 0;
        for (T currentValue: control.getItems()) {
            if (currentValue.equals(value)) {
                control.getSelectionModel().select(index);
            }
            index++;
        }
    }

    @Override
    public T getValue() {
        return control.getValue();
    }

    @Override
    public void onBind(Callback<T> callback) {
        control.valueProperty().addListener((observable, oldValue, newValue) -> {
            callback.onUpdate(newValue);
        });
    }
}
