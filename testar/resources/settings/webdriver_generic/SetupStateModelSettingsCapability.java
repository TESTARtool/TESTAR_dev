/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.statemodel.StateModelStorageBootstrap;

public final class SetupStateModelSettingsCapability extends SettingsCapability {

    private final SettingsCapability delegate;

    public SetupStateModelSettingsCapability(SettingsCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public Settings initializeSettings(Settings settings) {
        settings = delegate.initializeSettings(settings);
        prepareStateModelOrientDB(settings);
        return settings;
    }

    private void prepareStateModelOrientDB(Settings settings) {
        if (!settings.get(StateModelTags.StateModelEnabled, false)) {
            return;
        }

        String dataStoreType = settings.get(StateModelTags.DataStoreType, "");

        if (!"plocal".equalsIgnoreCase(dataStoreType)) {
            return;
        }

        String directoryPath = settings.get(StateModelTags.DataStoreDirectory, "");
        String database = settings.get(StateModelTags.DataStoreDB, "");
        String user = settings.get(StateModelTags.DataStoreUser, "");
        String password = settings.get(StateModelTags.DataStorePassword, "");

        if (directoryPath == null || directoryPath.isBlank()) {
            return;
        }

        if (database == null || database.isBlank()) {
            return;
        }

        if (user == null || user.isBlank()) {
            return;
        }

        if (password == null || password.isBlank()) {
            return;
        }

        StateModelStorageBootstrap.setupOrientDB(directoryPath, database, user, password);
    }
}
