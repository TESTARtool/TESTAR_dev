package org.testar.webstudio.workspace;

import java.util.Properties;

import org.junit.Test;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkspaceSettingsCatalogStateModelTest {

    @Test
    public void stateModelFormExposesOptionalStaticGraphExport() {
        Properties properties = new Properties();
        properties.setProperty("StateModelExportStaticGraph", "true");
        WorkspaceSettingsGroupDto group = WorkspaceSettingsCatalog.buildSettingsGroups(properties).stream()
                .filter(candidate -> candidate.id().equals("state-model")).findFirst().get();
        WorkspaceSettingDto setting = group.settings().stream()
                .filter(candidate -> candidate.key().equals("StateModelExportStaticGraph")).findFirst().get();

        assertEquals("boolean", setting.type());
        assertEquals("true", setting.value());
        assertTrue(setting.description().contains("Generate or CLI"));
        int size = group.settings().size();
        assertEquals("StateModelExportStaticGraph", group.settings().get(size - 2).key());
        WorkspaceSettingDto trees = group.settings().get(size - 1);
        assertEquals("StateModelExportStaticGraphIncludeWidgetTrees", trees.key());
        assertEquals("boolean", trees.type());
        assertEquals("false", trees.defaultValue());
    }
}
