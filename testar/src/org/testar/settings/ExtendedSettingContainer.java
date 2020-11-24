package org.testar.settings;

import java.util.Observable;
import java.util.Observer;

public class ExtendedSettingContainer<S extends ExtendedSettingBase<S>> implements Observer {
    // Access to the resource file which also holds the old value in case we need to update the settings.
    private final ExtendedSettingFile _file;
    // The actual settings.
    private final ExtendedSettingBase<S> _setting;

    public ExtendedSettingContainer(ExtendedSettingFile file, Class<S> clazz,
                                    IExtendedSettingDefaultValue<S> defaultFunctor) {
        _file = file;
        _setting = _file.load(clazz, defaultFunctor);
        _setting.addObserver(this);
    }

    /**
     * Get the settings.
     * @return The actual settings.
     */
    ExtendedSettingBase<S> GetSettings() {
        return _setting;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(_setting)) {
            _file.save(_setting);
        }
    }
}
