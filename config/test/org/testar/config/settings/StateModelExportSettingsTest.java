package org.testar.config.settings;

import java.util.Properties;

import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StateModelExportSettingsTest {

    @Test
    public void staticGraphExportDefaultsToDisabledAndCanBeEnabledInSettings() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "notepad.exe");
        Settings defaults = new Settings(SettingsDefaults.getSettingsDefaults(), properties);
        assertFalse(defaults.get(StateModelTags.StateModelExportStaticGraph));
        assertFalse(defaults.get(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees));

        properties.setProperty(StateModelTags.StateModelExportStaticGraph.name(), "true");
        properties.setProperty(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees.name(), "true");
        Settings enabled = new Settings(SettingsDefaults.getSettingsDefaults(), properties);
        assertTrue(enabled.get(StateModelTags.StateModelExportStaticGraph));
        assertTrue(enabled.toFileString().contains("StateModelExportStaticGraph = true"));
        assertTrue(enabled.get(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees));
        assertTrue(enabled.toFileString().contains("StateModelExportStaticGraphIncludeWidgetTrees = true"));
        assertFalse(enabled.get(StateModelTags.StateModelStoreWidgets));
    }
}
