package org.testar.webstudio.workspace;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

public class WorkspaceSettingsCatalogWebdriverTest {

    @Test
    public void chromeProfileSettingsAreAvailableInWebdriverGroup() {
        WorkspaceSettingsGroupDto webdriver = WorkspaceSettingsCatalog.buildSettingsGroups(new Properties())
                .stream()
                .filter(group -> group.id().equals("webdriver"))
                .findFirst()
                .orElseThrow();

        WorkspaceSettingDto userDataDir = webdriver.settings().stream()
                .filter(setting -> setting.key().equals(ConfigTags.WebChromeUserDataDir.name()))
                .findFirst()
                .orElseThrow();
        WorkspaceSettingDto profileDirectory = webdriver.settings().stream()
                .filter(setting -> setting.key().equals(ConfigTags.WebChromeProfileDirectory.name()))
                .findFirst()
                .orElseThrow();

        Assert.assertEquals("", userDataDir.defaultValue());
        Assert.assertEquals("", profileDirectory.defaultValue());
        Assert.assertEquals("string", userDataDir.type());
        Assert.assertEquals("string", profileDirectory.type());
    }
}
