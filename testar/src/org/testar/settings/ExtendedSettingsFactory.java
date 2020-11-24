package org.testar.settings;

import nl.ou.testar.visualvalidation.VisualValidationSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExtendedSettingsFactory {
    private static final ReentrantReadWriteLock _readWriteLock = new ReentrantReadWriteLock(true);
    // Store the created setting container because they old the loaded value.
    private static final List<ExtendedSettingBase<?>> _settingContainers = new ArrayList<>();
    private static String _absolutePath;

    /**
     * Initialize the extended settings factory.
     *
     * @param absolutePath Absolute path for extended settings file.
     */
    public static void Initialize(String absolutePath) {
        _settingContainers.clear();
        _absolutePath = absolutePath;
    }

    /**
     * Save all created extended settings.
     */
    public static void SaveAll() {
        _settingContainers.forEach(ExtendedSettingBase::Save);
    }

    @SuppressWarnings("unchecked")
    static <S extends ExtendedSettingBase<S>> S createSettings(Class<S> settingClass,
                                                               IExtendedSettingDefaultValue<S> defaultFunctor) {
        S setting = (S) _settingContainers.stream()
                .filter(element -> element.getClass() == settingClass)
                .findFirst()
                .orElse(null);

        // If this is the first time create the new container and settings.
        if (setting == null) {
            ExtendedSettingContainer<S> container = new ExtendedSettingContainer<>(
                    new ExtendedSettingFile(_absolutePath, _readWriteLock), settingClass, defaultFunctor);
            setting = (S) container.GetSettings();
            _settingContainers.add(setting);
        }
        return setting;
    }

    public static VisualValidationSettings createVisualValidationSettings() {
        return createSettings(VisualValidationSettings.class, VisualValidationSettings::CreateDefault);
    }

    public static ExampleSetting createTestSetting() {
        return createSettings(ExampleSetting.class, ExampleSetting::CreateDefault);
    }
}