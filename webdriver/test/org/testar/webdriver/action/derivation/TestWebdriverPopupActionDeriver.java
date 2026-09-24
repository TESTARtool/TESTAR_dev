package org.testar.webdriver.action.derivation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.webdriver.action.WdRemoteScrollClickAction;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.state.WdElement;
import org.testar.webdriver.state.WdRootElement;
import org.testar.webdriver.state.WdState;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;
import org.testar.webdriver.util.WebNavigationUtil;

public class TestWebdriverPopupActionDeriver {

    @Test
    public void derivesClickForVisibleEnabledWidgetWithMatchingAttribute() {
        WdState state = stateWithPopupWidget("accept-cookies", false, true, true);
        WebdriverPopupActionDeriver deriver = new WebdriverPopupActionDeriver(
                List.of("id=accept-cookies")
        );

        Set<Action> actions = deriver.derive(null, state, null);

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next() instanceof WdRemoteScrollClickAction);
        Assert.assertEquals("accept-cookies", actions.iterator().next()
                .get(Tags.OriginWidget).get(WdTags.WebId));
    }

    @Test
    public void returnsNoActionsWhenNoSelectorMatches() {
        WdState state = stateWithPopupWidget("accept-cookies", false, true, true);
        WebdriverPopupActionDeriver deriver = new WebdriverPopupActionDeriver(
                List.of("id=reject-cookies")
        );

        Assert.assertTrue(deriver.derive(null, state, null).isEmpty());
    }

    @Test
    public void ignoresHiddenNotDisplayedAndDisabledWidgets() {
        WdState hiddenState = stateWithPopupWidget("accept-cookies", true, true, true);
        WdState undisplayedState = stateWithPopupWidget("accept-cookies", false, false, true);
        WdState disabledState = stateWithPopupWidget("accept-cookies", false, true, false);
        WebdriverPopupActionDeriver deriver = new WebdriverPopupActionDeriver(
                List.of("id=accept-cookies")
        );

        Assert.assertTrue(deriver.derive(null, hiddenState, null).isEmpty());
        Assert.assertTrue(deriver.derive(null, undisplayedState, null).isEmpty());
        Assert.assertTrue(deriver.derive(null, disabledState, null).isEmpty());
    }

    @Test
    public void supportsAnyOfConfiguredAttributeSelectors() {
        WdState state = stateWithPopupWidget("reject-cookies", false, true, true);
        WebdriverPopupActionDeriver deriver = new WebdriverPopupActionDeriver(
                List.of("id=accept-cookies", "aria-label=Reject cookies")
        );
        WdWidget widget = (WdWidget) state.child(0);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", "reject-cookies");
        attributes.put("aria-label", "Reject cookies");
        widget.set(WdTags.WebAttributeMap, attributes);

        Assert.assertEquals(1, deriver.derive(null, state, null).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMalformedSelector() {
        new WebdriverPopupActionDeriver(List.of("accept-cookies"));
    }

    @Test
    public void deniedUrlRecoveryTakesPriorityOverPopupClick() {
        WdState state = stateWithPopupWidget("accept-cookies", false, true, true);
        Settings settings = settingsWithAllowedDomain("allowed.example");
        WebdriverForcedActionDeriver deriver = new WebdriverForcedActionDeriver(
                new WdDeniedUrlForcedActionDeriver(settings),
                new WebdriverPopupActionDeriver(List.of("id=accept-cookies"))
        );

        try (MockedStatic<WdDriver> mockedDriver = Mockito.mockStatic(WdDriver.class)) {
            mockedDriver.when(WdDriver::getCurrentUrl).thenReturn("https://denied.example/path");
            mockedDriver.when(WdDriver::getWindowHandles).thenReturn(Set.of("main"));

            Set<Action> actions = deriver.derive(null, state, null);

            Assert.assertEquals(1, actions.size());
            Assert.assertEquals("WdHistoryBackAction", actions.iterator().next().getClass().getSimpleName());
        }
    }

    private WdState stateWithPopupWidget(String id,
                                        boolean hidden,
                                        boolean displayed,
                                        boolean enabled) {
        WdRootElement rootElement = new WdRootElement();
        WdState state = new WdState(rootElement);
        WdElement element = new WdElement(rootElement, rootElement);
        element.id = id;
        element.tagName = "button";
        element.rect = Rect.from(0, 0, 100, 30);
        WdWidget widget = new WdWidget(state, state, element);
        widget.set(Tags.Desc, "Cookie consent button");
        widget.set(WdTags.WebAttributeMap, new HashMap<>(Map.of("id", id)));
        widget.set(WdTags.WebIsHidden, hidden);
        widget.set(WdTags.WebIsDisplayed, displayed);
        widget.set(WdTags.WebIsEnabled, enabled);
        return state;
    }

    private Settings settingsWithAllowedDomain(String domain) {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnector.name(), Settings.SUT_CONNECTOR_WEBDRIVER);
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "\"https://" + domain + "\"");
        Settings settings = new Settings(properties);
        settings.set(ConfigTags.WebDomainsAllowed, new ArrayList<>(List.of(domain)));
        settings.set(ConfigTags.WebPathsAllowed, "");
        settings.set(ConfigTags.WebDeniedExtensions, new ArrayList<>());
        WebNavigationUtil.addInitialAllowedDomains(settings, "https://" + domain);
        return settings;
    }
}
