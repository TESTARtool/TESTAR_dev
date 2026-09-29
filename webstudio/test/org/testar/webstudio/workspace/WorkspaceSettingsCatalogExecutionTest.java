package org.testar.webstudio.workspace;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

public class WorkspaceSettingsCatalogExecutionTest {

    @Test
    public void visualizeActionsBelongsToSutExecution() {
        List<WorkspaceSettingsGroupDto> groups = WorkspaceSettingsCatalog.buildSettingsGroups(new Properties());
        WorkspaceSettingsGroupDto executionGroup = groups.stream()
                .filter(group -> group.id().equals("execution"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SUT Execution settings group not found"));
        WorkspaceSettingDto visualizeActions = executionGroup.settings().stream()
                .filter(setting -> setting.key().equals("VisualizeActions"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("VisualizeActions setting not found"));

        Assert.assertEquals("boolean", visualizeActions.type());
        Assert.assertTrue(visualizeActions.description().contains("Generate mode"));
        Assert.assertFalse(visualizeActions.description().contains("Spy"));
    }
}
