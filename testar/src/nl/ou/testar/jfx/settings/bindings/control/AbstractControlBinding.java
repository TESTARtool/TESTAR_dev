package nl.ou.testar.jfx.settings.bindings.control;

import javafx.scene.control.Control;

public abstract class AbstractControlBinding<C extends Control, T> implements ControlBinding<T> {

    protected C control;

    public AbstractControlBinding(C control) {
        this.control = control;
    }
}
