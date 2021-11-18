package nl.ou.testar.jfx.settings.bindings;

import javafx.scene.control.*;
import org.fruit.alayer.Tag;
import org.fruit.monkey.Settings;

public abstract class ConfigTagBinding<C extends Control, T> extends AbstractConfigBinding<C, T> {

    public enum Type {
        FIELD_STRING, FIELD_INT, FIELD_DOUBLE, TEXT_INPUT, CHECK_BOX, COMBO_BOX
    }

    protected Settings settings;
    protected Tag<T> tag;

    public ConfigTagBinding(Settings settings, C control, Tag<T> tag) {
        super(control);
        this.settings = settings;
        this.tag = tag;
        this.value = settings.get(tag);
    }

    public Tag<T> getTag() {
        return tag;
    }

    @Override
    protected T getTargetValue() {
        return settings.get(tag);
    }

    @Override
    protected void setTargetValue(T value) {
        settings.set(tag, value);
    }

    public static ConfigTagBinding bind(Settings settings, Control control, Tag tag, Type type) {
        ConfigTagBinding binding;
        switch (type) {
            case FIELD_STRING:
                binding = new StringFieldTagBinding(settings, (TextField) control, tag);
                break;
            case FIELD_INT:
                binding = new IntegerFieldTagBinding(settings, (TextField) control, tag);
                break;
            case FIELD_DOUBLE:
                binding = new DoubleInputBinding(settings, (TextField) control, tag);
                break;
            case TEXT_INPUT:
                binding = new TextInputBinding(settings, (TextInputControl) control, tag);
                break;
            case CHECK_BOX:
                binding = new CheckTagBinding(settings, (CheckBox) control, tag);
                break;
            default: // COMBO_BOX
                binding = new ComboBoxTagBinding(settings, (ComboBox) control, tag);
                break;
        }
        binding.onBind();
        return binding;
    }
}
