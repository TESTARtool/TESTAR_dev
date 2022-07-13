package nl.ou.testar.jfx.settings.bindings.data;

import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Tag;

public class TagDataSource<T> implements DataSource<T> {

    private Settings settings;
    private Tag<T> tag;
    private T defaultValue = null;

    public TagDataSource(Settings settings, Tag<T> tag) {
        this.settings = settings;
        this.tag = tag;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class getDataType() {
        return tag.type();
    }

    @Override
    public T getData() {
        return settings.get(tag, defaultValue);
    }

    @Override
    public void setData(T data) {
        settings.set(tag, data);
    }
}
