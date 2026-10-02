package org.testar.statemodel;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.core.CodingManager;
import org.testar.core.state.StateIdentity;
import org.testar.core.tag.Tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StateModelManagerFactoryTest {

    private Tag<?>[] previousAbstractTags;

    @Before
    public void preserveConfiguration() {
        previousAbstractTags = CodingManager.getCustomTagsForAbstractId();
    }

    @After
    public void restoreConfiguration() {
        CodingManager.setCustomTagsForAbstractId(previousAbstractTags);
    }

    @Test
    public void modelUsesItsSettingsInsteadOfGlobalCodingConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnector.name(), Settings.SUT_CONNECTOR_WEBDRIVER);
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "https://example.org");
        Settings settings = new Settings(SettingsDefaults.getSettingsDefaults(), properties);
        settings.set(StateModelTags.StateModelInference, true);
        settings.set(StateModelTags.DataStoreMode, "none");
        settings.set(ConfigTags.AbstractStateAttributes, List.of("WebWidgetId"));
        CodingManager.setCustomTagsForAbstractId(new Tag<?>[0]);

        ModelManager first = (ModelManager) StateModelManagerFactory.getStateModelManager("application", "1", settings);
        assertEquals(StateIdentity.fromAttributes(List.of("WebWidgetId")).modelHash("application", "1"),
                first.getModelIdentifier());

        CodingManager.initCodingManager(List.of("AndroidWidgetResourceId"));
        ModelManager same = (ModelManager) StateModelManagerFactory.getStateModelManager("application", "1", settings);
        assertEquals(first.getModelIdentifier(), same.getModelIdentifier());

        settings.set(ConfigTags.AbstractStateAttributes, List.of("WidgetControlType"));
        ModelManager other = (ModelManager) StateModelManagerFactory.getStateModelManager("application", "1", settings);
        assertNotEquals(first.getModelIdentifier(), other.getModelIdentifier());
    }
}
