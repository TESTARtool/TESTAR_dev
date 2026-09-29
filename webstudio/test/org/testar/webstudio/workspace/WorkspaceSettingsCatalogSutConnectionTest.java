package org.testar.webstudio.workspace;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

public class WorkspaceSettingsCatalogSutConnectionTest {

    @Test
    public void exposesDesktopConnectionSettings() {
        Properties settingsProperties = new Properties();
        settingsProperties.setProperty("SUTProcesses", "notepad.*");
        settingsProperties.setProperty("JavaAccessBridge", "true");

        List<WorkspaceSettingsGroupDto> groups = WorkspaceSettingsCatalog.buildSettingsGroups(settingsProperties);
        WorkspaceSettingsGroupDto connectionGroup = groups.stream()
                .filter(group -> group.id().equals("sut-connection"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SUT Connection group not found"));

        WorkspaceSettingDto sutProcesses = findSetting(connectionGroup, "SUTProcesses");
        WorkspaceSettingDto javaAccessBridge = findSetting(connectionGroup, "JavaAccessBridge");

        Assert.assertEquals("notepad.*", sutProcesses.value());
        Assert.assertEquals("string", sutProcesses.type());
        Assert.assertTrue(sutProcesses.regexCapable());
        Assert.assertEquals("true", javaAccessBridge.value());
        Assert.assertEquals("boolean", javaAccessBridge.type());
    }

    private WorkspaceSettingDto findSetting(WorkspaceSettingsGroupDto group, String key) {
        return group.settings().stream()
                .filter(setting -> setting.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Setting not found: " + key));
    }
}
