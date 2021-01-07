package org.testar.settings;


import java.io.Serializable;
import java.util.Observable;

public abstract class ExtendedSettingBase<T> extends Observable implements IExtendedSetting, Comparable<T>, Serializable {
    /**
     * Notify the {@link IExtendedSettingContainer} that the specialization of this class needs to be saved.
     */
    @Override
    public void Save() {
        setChanged();
        notifyObservers();
    }
}
