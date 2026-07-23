package org.testar.plugin;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.TestarMode;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.core.CodingManager;
import org.testar.core.tag.Tag;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.configuration.PolicySessionConfiguration;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.statemodel.DummyModelManager;

public class PlatformOrchestratorStateModelTest {

    @Test
    public void initializesAbstractStateAttributesBeforeCreatingStateModelManager() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.Mode, TestarMode.Generate);
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER);
        settings.set(ConfigTags.SUTConnectorValue, "https://example.test");
        settings.set(ConfigTags.ApplicationName, "webdriver_generic");
        settings.set(ConfigTags.ApplicationVersion, "1");
        settings.set(ConfigTags.AbstractStateAttributes, List.of("WidgetControlType", "WebWidgetId"));
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStoreMode, "none");

        CodingManager.setCustomTagsForAbstractId(new Tag<?>[0]);
        PlatformSessionSpecification sessionSpec = PlatformSessionSpecification.builder(
                OperatingSystems.WEBDRIVER,
                PlatformSessionSpecification.TargetType.EXECUTABLE,
                "https://example.test",
                settings
        ).build();

        PlatformOrchestrator.resolve(
                sessionSpec,
                PolicySessionConfiguration.defaults(),
                ServiceSessionConfiguration.defaults()
        );

        assertTrue(CodingManager.getCustomTagsForAbstractId().length > 0);
    }

    @Test
    public void spyModeUsesDummyStateModelManagerWhenStateModelInferenceIsEnabled() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.Mode, TestarMode.Spy);
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER);
        settings.set(ConfigTags.SUTConnectorValue, "https://example.test");
        settings.set(ConfigTags.ApplicationName, "webdriver_generic");
        settings.set(ConfigTags.ApplicationVersion, "1");
        settings.set(ConfigTags.AbstractStateAttributes, List.of("WidgetControlType", "WebWidgetId"));
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStore, "OrientDB");
        settings.set(StateModelTags.DataStoreType, "plocal");
        settings.set(StateModelTags.DataStoreDirectory, "missing-orientdb-directory");
        settings.set(StateModelTags.DataStoreDB, "testar");
        settings.set(StateModelTags.DataStoreUser, "testar");
        settings.set(StateModelTags.DataStorePassword, "testar");
        settings.set(StateModelTags.DataStoreMode, "instant");

        PlatformSessionSpecification sessionSpec = PlatformSessionSpecification.builder(
                OperatingSystems.WEBDRIVER,
                PlatformSessionSpecification.TargetType.EXECUTABLE,
                "https://example.test",
                settings
        ).build();

        PlatformServices services = PlatformOrchestrator.resolve(
                sessionSpec,
                PolicySessionConfiguration.defaults(),
                ServiceSessionConfiguration.defaults()
        );

        assertTrue(services.stateModelManager() instanceof DummyModelManager);
    }

    @Test
    public void explicitSpySessionUsesDummyStateModelManagerWhenStateModelInferenceIsEnabled() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.Mode, TestarMode.Spy);
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER);
        settings.set(ConfigTags.SUTConnectorValue, "https://example.test");
        settings.set(ConfigTags.AbstractStateAttributes, List.of("WidgetControlType", "WebWidgetId"));
        settings.set(StateModelTags.StateModelInference, true);

        PlatformSessionSpecification sessionSpec = PlatformSessionSpecification.builder(
                OperatingSystems.WEBDRIVER,
                PlatformSessionSpecification.TargetType.EXECUTABLE,
                "https://example.test",
                settings
        ).build();

        PlatformServices services = PlatformOrchestrator.resolve(
                sessionSpec,
                PolicySessionConfiguration.defaults(),
                ServiceSessionConfiguration.defaults(),
                new DummyModelManager()
        );

        assertTrue(services.stateModelManager() instanceof DummyModelManager);
    }

    private static Settings defaultSettings() {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnector.name(), Settings.SUT_CONNECTOR_WEBDRIVER);
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "https://example.test");
        return new Settings(SettingsDefaults.getSettingsDefaults(), properties);
    }
}
