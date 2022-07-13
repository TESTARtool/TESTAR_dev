package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.Control;

public abstract class AbstractConfigBinding<C extends Control, T> {

    protected C control;
    protected T value;

    public AbstractConfigBinding(C control) {
        this.control = control;
    }

    public C getControl() {
        return control;
    }

    protected abstract T getTargetValue();
    protected abstract void setTargetValue(T value);

    public void save() {
        setTargetValue(value);
    };

    public boolean needsSave() {
        T targetValue = getTargetValue();
        return !((value == null && targetValue == null) || value.equals(targetValue));
    }

    public abstract void onBind();
}
