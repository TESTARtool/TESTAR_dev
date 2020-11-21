package org.testar.settings;

public interface IExtendedSettingDefaultValue<T> {
    /**
     * Functor to create an initialized object T with default values.
     * Should not be implemented by specializations of {@link ExtendedSettings}.
     *
     * @return Object T initialized with default values.
     */
    T CreateDefault();
}
