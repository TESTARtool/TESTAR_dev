package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.CheckBox;

public class CheckBoxBinding extends AbstractControlBinding<CheckBox, Boolean> {

    public CheckBoxBinding(CheckBox control) {
        super(control);
    }

    @Override
    public void setValue(Boolean value) {
        control.setSelected(value);
    }

    @Override
    public Boolean getValue() {
        return control.isSelected();
    }

    @Override
    public void onBind(Callback<Boolean> callback) {
        control.selectedProperty().addListener((observable, oldValue, newValue) -> callback.onUpdate(newValue));
    }
}
