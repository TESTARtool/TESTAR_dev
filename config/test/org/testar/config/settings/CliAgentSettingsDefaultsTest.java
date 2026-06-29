package org.testar.config.settings;

import org.junit.Test;
import org.testar.config.CliStateProjectionMode;
import org.testar.config.ConfigTags;
import org.testar.core.Assert;

import java.util.Properties;

public class CliAgentSettingsDefaultsTest {

    @Test
    public void defaultCliStateProjectionModeIsInteractiveSemanticWidgets() {
        Settings settings = new Settings(SettingsDefaults.getSettingsDefaults(), verifiedProperties());

        Assert.isTrue(settings.get(ConfigTags.CliStateProjectionMode)
                .equals(CliStateProjectionMode.INTERACTIVE_SEMANTIC_WIDGETS));
    }

    @Test
    public void parsesCliStateProjectionModeFromSettingsValue() {
        CliStateProjectionMode parsedMode = Settings.parse(
                "ACTIONABLE_WIDGETS",
                ConfigTags.CliStateProjectionMode
        );

        Assert.isTrue(parsedMode.equals(CliStateProjectionMode.ACTIONABLE_WIDGETS));
    }

    @Test
    public void defaultAgentCliSettingsAreAvailable() {
        Settings settings = new Settings(SettingsDefaults.getSettingsDefaults(), verifiedProperties());

        Assert.isTrue(settings.get(ConfigTags.AgentCLIApiKeyEnvVar).equals("OPENAI_API_KEY"));
        Assert.isTrue(settings.get(ConfigTags.AgentCLIModel).equals("gpt-5.4-mini"));
        Assert.isTrue(settings.get(ConfigTags.AgentCLIReasoningEffort).equals("medium"));
        Assert.isTrue(settings.get(ConfigTags.AgentCLISandboxMode).equals("danger-full-access"));
        Assert.isTrue(settings.get(ConfigTags.AgentCLIApprovalPolicy).equals("never"));
        Assert.isTrue(!settings.get(ConfigTags.AgentCLINetworkAccessEnabled));
        Assert.isTrue(settings.get(ConfigTags.AgentCLISkipGitRepoCheck));
    }

    private Properties verifiedProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "notepad.exe");
        return properties;
    }
}
