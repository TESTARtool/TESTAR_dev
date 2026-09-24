package org.testar.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testar.config.settings.Settings;
import org.testar.core.Pair;

public final class SettingsTestSupport {

    private SettingsTestSupport() {
    }

    public static Settings newSettings() {
        return new Settings(validSutConnectorSettings(), new Properties());
    }

    public static List<Pair<?, ?>> withValidSutConnector(List<Pair<?, ?>> settings) {
        List<Pair<?, ?>> configuredSettings = new ArrayList<>(settings);
        configuredSettings.addAll(validSutConnectorSettings());
        return configuredSettings;
    }

    private static List<Pair<?, ?>> validSutConnectorSettings() {
        List<Pair<?, ?>> settings = new ArrayList<>();
        settings.add(Pair.from(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
        settings.add(Pair.from(ConfigTags.SUTConnectorValue, "test-command"));
        return settings;
    }
}
