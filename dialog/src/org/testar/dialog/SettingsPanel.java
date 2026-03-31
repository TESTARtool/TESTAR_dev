/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import org.testar.config.settings.Settings;

import javax.swing.*;

/**
 * Abstract class for all common functionality for a SettingsPanel.
 */
public abstract class SettingsPanel extends JPanel {
    /**
     * Populate the fields from Settings structure.
     *
     * @param settings The settings to load.
     */
    public abstract void populateFrom(final Settings settings);

    /**
     * Retrieve information from the GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    public abstract void extractInformation(final Settings settings);

    /**
     * Validate that the settings are valid.
     */
    public void checkSettings() {
    }
}
