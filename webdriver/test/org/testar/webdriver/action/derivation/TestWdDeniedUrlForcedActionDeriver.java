package org.testar.webdriver.action.derivation;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.stub.StateStub;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.util.WebNavigationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class TestWdDeniedUrlForcedActionDeriver {

    @Test
    public void derivesNoForcedActionAfterAddingInitialWebDomains() {
        Settings settings = settingsWithDomains(List.of("www.ou.nl"));
        WdDeniedUrlForcedActionDeriver forcedActionsDeriver = new WdDeniedUrlForcedActionDeriver(settings);

        WebNavigationUtil.addInitialAllowedDomains(settings, "https://para.testar.org/parabank");

        try (MockedStatic<WdDriver> mockedDriver = Mockito.mockStatic(WdDriver.class)) {
            mockedDriver.when(WdDriver::getCurrentUrl).thenReturn("https://para.testar.org/parabank");
            mockedDriver.when(WdDriver::getWindowHandles).thenReturn(Set.of("main"));

            Set<Action> forcedActions = forcedActionsDeriver.derive(null, new StateStub(), null);

            Assert.assertTrue(forcedActions.isEmpty());
        }
    }

    @Test
    public void derivesForcedActionOutsideWebDomain() {
        Settings settings = settingsWithDomains(List.of("www.ou.nl"));
        WdDeniedUrlForcedActionDeriver forcedActionsDeriver = new WdDeniedUrlForcedActionDeriver(settings);

        WebNavigationUtil.addInitialAllowedDomains(settings, "https://para.testar.org/parabank");

        try (MockedStatic<WdDriver> mockedDriver = Mockito.mockStatic(WdDriver.class)) {
            mockedDriver.when(WdDriver::getCurrentUrl).thenReturn("https://parasoft.parabank.com");
            mockedDriver.when(WdDriver::getWindowHandles).thenReturn(Set.of("main"));

            Set<Action> forcedActions = forcedActionsDeriver.derive(null, new StateStub(), null);

            Assert.assertFalse(forcedActions.isEmpty());
            Assert.assertTrue(forcedActions.size() == 1);
            Assert.assertTrue(forcedActions.iterator().next().getClass().getSimpleName().equals("WdHistoryBackAction"));
        }
    }

    private static Settings settingsWithDomains(List<String> domains) {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnector.name(), Settings.SUT_CONNECTOR_WEBDRIVER);
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "\"https://para.testar.org\"");
        Settings settings = new Settings(new Properties(properties));
        settings.set(ConfigTags.WebDomainsAllowed, new ArrayList<>(domains));
        settings.set(ConfigTags.WebDeniedExtensions, new ArrayList<>());
        settings.set(ConfigTags.WebPathsAllowed, "");
        return settings;
    }
}
