package org.fruit.monkey;

import javax.swing.*;

/**
 * @brief Abstract class for all common functionality for a SettingsPanel.
 */
public abstract class SettingsPanel extends JPanel {
    /**
     * Populate the fields from Settings structure.
     * @param settings The settings to load.
     */
    public abstract void populateFrom(final Settings settings);

    /**
     * Retrieve information from the GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public abstract void extractInformation(final Settings settings);

    /**
     * Validate that the settings are valid.
     */
    public void checkSettings() {};
}
