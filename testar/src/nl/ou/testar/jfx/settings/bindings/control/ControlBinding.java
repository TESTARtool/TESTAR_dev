package nl.ou.testar.jfx.settings.bindings.control;

public interface ControlBinding<T> {

    interface Callback<S> {
        void onUpdate(S value);
    }

    void setValue(T value);
    T getValue();
    void onBind(Callback<T> callback);
}
