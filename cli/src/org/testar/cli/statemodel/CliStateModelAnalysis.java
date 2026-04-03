/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli.statemodel;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.testar.cli.settings.CliSettingsLoader;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.webserver.JettyServer;
import org.testar.statemodel.persistence.orientdb.entity.Config;

public final class CliStateModelAnalysis {

    private final PrintStream output;

    public CliStateModelAnalysis(PrintStream output) {
        this.output = Assert.notNull(output);
    }

    public int run() {
        Settings settings = CliSettingsLoader.load();
        List<String> missingSettings = validateSettings(settings);
        if (!missingSettings.isEmpty()) {
            output.println("stateModelServer failed: missing settings");
            for (String missingSetting : missingSettings) {
                output.println(missingSetting);
            }
            return 1;
        }

        try {
            Config config = new Config();
            config.setConnectionType(settings.get(StateModelTags.DataStoreType, ""));
            config.setServer(settings.get(StateModelTags.DataStoreServer, ""));
            config.setDatabase(settings.get(StateModelTags.DataStoreDB, ""));
            config.setUser(settings.get(StateModelTags.DataStoreUser, ""));
            config.setPassword(settings.get(StateModelTags.DataStorePassword, ""));
            config.setDatabaseDirectory(settings.get(StateModelTags.DataStoreDirectory, ""));
            AnalysisManager analysisManager = new AnalysisManager(config, resolveGraphsOutputDirectory(settings));
            JettyServer jettyServer = new JettyServer();
            jettyServer.start(resolveGraphsOutputDirectory(settings), analysisManager);
            openBrowser();
            return 0;
        } catch (Exception exception) {
            output.println("stateModelServer failed: " + exception.getMessage());
            return 1;
        }
    }

    private List<String> validateSettings(Settings settings) {
        List<String> missingSettings = new ArrayList<>();
        if (!settings.get(StateModelTags.StateModelEnabled, false)) {
            missingSettings.add("StateModelEnabled=false");
        }

        String dataStoreType = settings.get(StateModelTags.DataStoreType, "");
        if (dataStoreType.isBlank()) {
            missingSettings.add("DataStoreType");
        }

        if (settings.get(StateModelTags.DataStoreDB, "").isBlank()) {
            missingSettings.add("DataStoreDB");
        }

        if (settings.get(StateModelTags.DataStoreUser, "").isBlank()) {
            missingSettings.add("DataStoreUser");
        }

        if (settings.get(ConfigTags.OutputDir, "").isBlank()) {
            missingSettings.add("OutputDir");
        }

        if ("plocal".equalsIgnoreCase(dataStoreType)
                && settings.get(StateModelTags.DataStoreDirectory, "").isBlank()) {
            missingSettings.add("DataStoreDirectory");
        }

        if ("remote".equalsIgnoreCase(dataStoreType)
                && settings.get(StateModelTags.DataStoreServer, "").isBlank()) {
            missingSettings.add("DataStoreServer");
        }

        return missingSettings;
    }

    private String resolveGraphsOutputDirectory(Settings settings) {
        String outputDirectory = settings.get(ConfigTags.OutputDir, "");
        if (!outputDirectory.endsWith(File.separator)) {
            outputDirectory += File.separator;
        }
        return outputDirectory + "graphs" + File.separator;
    }

    private void openBrowser() {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI uri = new URI("http://localhost:8090/models");
            desktop.browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
