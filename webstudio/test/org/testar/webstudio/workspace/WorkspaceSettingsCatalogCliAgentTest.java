package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.WorkspaceSettingDto;
import org.testar.webstudio.api.dto.WorkspaceSettingsGroupDto;

import java.util.List;
import java.util.Properties;

public class WorkspaceSettingsCatalogCliAgentTest {

    @Test
    public void includesAgentCliSettingsGroup() {
        List<WorkspaceSettingsGroupDto> groups = WorkspaceSettingsCatalog.buildSettingsGroups(new Properties());
        WorkspaceSettingsGroupDto agentCliGroup = groups.stream()
                .filter(group -> group.id().equals("agent-cli"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Agent CLI settings group not found"));

        Assert.assertEquals("Agent CLI", agentCliGroup.title());
        Assert.assertTrue(containsSetting(agentCliGroup, "CliStateProjectionMode"));
        Assert.assertTrue(containsSetting(agentCliGroup, "AgentCLIApiKeyEnvVar"));
        Assert.assertTrue(containsSetting(agentCliGroup, "AgentCLIPromptText"));
    }

    @Test
    public void cliProjectionModeIsRenderedAsEnumDropdown() {
        WorkspaceSettingDto setting = findSetting("CliStateProjectionMode");

        Assert.assertEquals("enum", setting.type());
        Assert.assertFalse(setting.options().isEmpty());
        Assert.assertTrue(setting.options().contains("INTERACTIVE_SEMANTIC_WIDGETS"));
        Assert.assertEquals("INTERACTIVE_SEMANTIC_WIDGETS", setting.defaultValue());
    }

    @Test
    public void agentCliStringDropdownsDeclareOptions() {
        Assert.assertTrue(findSetting("AgentCLIReasoningEffort").options().contains("medium"));
        Assert.assertTrue(findSetting("AgentCLISandboxMode").options().contains("danger-full-access"));
        Assert.assertTrue(findSetting("AgentCLIApprovalPolicy").options().contains("never"));
    }

    private WorkspaceSettingDto findSetting(String settingKey) {
        return WorkspaceSettingsCatalog.buildSettingsGroups(new Properties())
                .stream()
                .flatMap(group -> group.settings().stream())
                .filter(setting -> setting.key().equals(settingKey))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Setting not found: " + settingKey));
    }

    private boolean containsSetting(WorkspaceSettingsGroupDto group, String settingKey) {
        return group.settings()
                .stream()
                .anyMatch(setting -> setting.key().equals(settingKey));
    }
}
