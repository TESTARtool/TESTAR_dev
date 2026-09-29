package org.testar.webstudio.workspace;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

// Verifies WS-FUNC-TEST-SETTINGS-004.
public class SpyTagCatalogTest {

    @Test
    public void exposesKnownSpyTagsWithDefaultsAndPlatformGroups() {
        List<SpyTagCatalog.TagOption> options = SpyTagCatalog.options();
        Set<String> names = options.stream().map(SpyTagCatalog.TagOption::key).collect(Collectors.toSet());

        Assert.assertEquals(names.size(), options.size());
        Assert.assertTrue(options.stream().anyMatch(option ->
                option.key().equals("Title") && option.group().equals("Common") && option.defaultSelected()));
        Assert.assertTrue(options.stream().anyMatch(option -> option.group().equals("Windows")));
        Assert.assertTrue(options.stream().anyMatch(option -> option.group().equals("WebDriver")));
        Assert.assertTrue(options.stream().anyMatch(option ->
                option.key().equals("WebTagName") && !option.defaultSelected()));
    }

    @Test
    public void spyVisualizationSettingsAreAvailableInAdvancedSettingsCatalog() {
        WorkspaceSettingsGroupDto group = WorkspaceSettingsCatalog.buildSettingsGroups(new Properties()).stream()
                .filter(candidate -> candidate.id().equals("spy"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Spy Visualization group not found"));

        WorkspaceSettingDto spyAttributes = group.settings().stream()
                .filter(setting -> setting.key().equals("SpyTagAttributes"))
                .findFirst()
                .orElseThrow();

        Assert.assertTrue(group.settings().stream().anyMatch(setting -> setting.key().equals("RefreshSpyCanvas")));
        Assert.assertEquals(spyAttributes.defaultValue(), spyAttributes.value());
    }

    @Test
    public void explicitlyEmptySpySelectionDoesNotRestoreDefaults() {
        Properties settings = new Properties();
        settings.setProperty("SpyTagAttributes", "");

        WorkspaceSettingsGroupDto group = WorkspaceSettingsCatalog.buildSettingsGroups(settings).stream()
                .filter(candidate -> candidate.id().equals("spy"))
                .findFirst()
                .orElseThrow();

        Assert.assertEquals("", group.settings().stream()
                .filter(setting -> setting.key().equals("SpyTagAttributes"))
                .findFirst()
                .orElseThrow()
                .value());
    }
}
