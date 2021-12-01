package nl.ou.testar.jfx.settings.bindings;

import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.*;
import nl.ou.testar.jfx.settings.bindings.control.*;
import nl.ou.testar.jfx.settings.bindings.data.DataSource;
import nl.ou.testar.jfx.settings.bindings.data.TagDataSource;
import org.fruit.alayer.Tag;
import org.fruit.monkey.Settings;

public class ConfigBinding<T> {

    public enum GenericType {
        FIELD_STRING, FIELD_INT, FIELD_DOUBLE, TEXT_INPUT, CHECK_BOX, COMBO_BOX
    }

    private ControlBinding<T> controlBinding;
    private DataSource<T> dataSource;

    private T value;

    private ConfigBinding(ControlBinding<T> controlBinding, DataSource<T> dataSource) {
        this.controlBinding = controlBinding;
        this.dataSource = dataSource;
        this.value = dataSource.getData();
    }

    public void bind() {
        controlBinding. setValue(value);
        controlBinding.onBind(value -> {
            setValue(value);
        });
    }

    public void setValue(T value) {
        this.value = value;
        controlBinding.setValue(value);
    }

    public T getValue() {
        return value;
    }

    public T resetValue() {
        setValue(dataSource.getData());;
        return value;
    }

    public boolean needsSave() {
        T data = dataSource.getData();
        if (value == null) {
            return dataSource.getData() != null;
        }
        return !value.equals(data);
    }

    public void save() {
        dataSource.setData(value);
    }


    public static class Builder<T> {
        private Control control;
        private GenericType genericType;
        private Settings settings;
        private Tag<T> tag;

        private ControlBinding<T> customControlBinding;
        private DataSource<T> customDataSource;

        public Builder<T> withControl(Control control) {
            this.control = control;
            return this;
        }

        public Builder<T> withGenericType(GenericType genericType) {
            this.genericType = genericType;
            return this;
        }

        public Builder<T> withSettings(Settings settings) {
            this.settings = settings;
            return this;
        }

        public Builder<T> withTag(Tag tag) {
            this.tag = tag;
            return this;
        }

        public Builder<T> withCustomControlBinding(ControlBinding<T> customControlBinding) {
            this.customControlBinding = customControlBinding;
            return this;
        }

        public Builder<T> withCustomDataSource(DataSource<T> dataSource) {
            this.customDataSource = dataSource;
            return this;
        }

        public ConfigBinding<T> build() throws ConfigBindingException {

            ControlBinding controlBinding = customControlBinding;
            DataSource dataSource = customDataSource;

            if (dataSource == null) {
                if (settings == null) {
                    throw new ConfigBindingException("Either settings object or custom data source should be set");
                }
                if (tag == null) {
                    throw new ConfigBindingException("Either settings tag or custom data source should be set");
                }

                TagDataSource tagDataSource = new TagDataSource(settings, tag);
                if (genericType == GenericType.FIELD_STRING || genericType == GenericType.TEXT_INPUT) {
                    tagDataSource.setDefaultValue("");
                }
                else if (genericType == GenericType.FIELD_INT) {
                    tagDataSource.setDefaultValue(new Integer(0));
                }
                else if (genericType == GenericType.FIELD_DOUBLE) {
                    tagDataSource.setDefaultValue(new Double(0));
                }
                else if (genericType == GenericType.CHECK_BOX) {
                    tagDataSource.setDefaultValue(new Boolean(false));
                }
                dataSource = tagDataSource;
            }

            if (controlBinding == null) {
                if (control == null) {
                    throw new ConfigBindingException("Control not set");
                }
                if (genericType == null) {
                    throw new ConfigBindingException("Either generic type or custom control binding should be set");
                }
                switch (genericType) {
                    case TEXT_INPUT:
                        assertControlTypeMatch(control, TextArea.class);
                        assertDataTypeMatch(dataSource, String.class);
                        controlBinding = new TextAreaBinding((TextArea) control);
                        break;
                    case FIELD_STRING:
                        assertControlTypeMatch(control, TextField.class);
                        assertDataTypeMatch(dataSource, String.class);
                        controlBinding = new StringFieldBinding((TextField) control);
                        break;
                    case FIELD_INT:
                        assertControlTypeMatch(control, TextField.class);
                        assertDataTypeMatch(dataSource, Integer.class);
                        controlBinding = new IntegerFieldBinding((TextField) control);
                        break;
                    case FIELD_DOUBLE:
                        assertControlTypeMatch(control, TextField.class);
                        assertDataTypeMatch(dataSource, Double.class);
                        controlBinding = new DoubleFieldBinding((TextField) control);
                        break;
                    case CHECK_BOX:
                        assertControlTypeMatch(control, CheckBox.class);
                        assertDataTypeMatch(dataSource, Boolean.class);
                        controlBinding = new CheckBoxBinding((CheckBox) control);
                        break;
                    case COMBO_BOX:
                        assertControlTypeMatch(control, ComboBox.class);
                        controlBinding = new ComboBoxBinding<T>((ComboBox) control);
                        break;
                    default:
                        throw new ConfigBindingException(String.format("Unknown generic type: %s", genericType.toString()));
                }
            }

            ConfigBinding configBinding = new ConfigBinding<T>(controlBinding, dataSource);

            // Reset builder to reuse it
            control = null;
            genericType = null;
            settings = null;
            tag = null;
            customControlBinding = null;
            customDataSource = null;

            return configBinding;
        }
        private void assertControlTypeMatch(Control control, Class expectedControlType)
                throws ConfigBindingException {
            if (!expectedControlType.isInstance(control)) {
                throw new ConfigBindingException(String.format("Control type mismatch: expected %s, actual %s",
                        expectedControlType.getSimpleName(), control.getClass().getSimpleName()));
            }
        };
        private void assertDataTypeMatch(DataSource dataSource, Class expectedTagType)
                throws ConfigBindingException {
            if (!dataSource.getDataType().isAssignableFrom(expectedTagType)) {
                throw new ConfigBindingException(String.format("Data type mismatch: expected %s, actual %s",
                        expectedTagType.getSimpleName(), dataSource.getDataType().getSimpleName()));
            }
        }
    }
}
