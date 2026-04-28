package org.testar.webdriver.action;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.composite.CompositeAtCanvasPolicy;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeSelectablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.webdriver.action.derivation.WebdriverWidgetActionDeriver;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.policy.ConfigurableWebdriverTypeableClassPolicy;
import org.testar.webdriver.policy.WebdriverClickablePolicy;
import org.testar.webdriver.policy.WebdriverSelectablePolicy;
import org.testar.webdriver.policy.WebdriverScrollablePolicy;
import org.testar.webdriver.policy.WebdriverTypeablePolicy;
import org.testar.webdriver.stub.WdWidgetStub;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverWidgetActionDeriver {

    @Test
    public void testDeriveAddsTypeActionForInputWithConfiguredTypeableClass() {
        WdWidgetStub wdWidget = new WdWidgetStub("input_amount", "amount", WdRoles.WdINPUT, "input");
        wdWidget.set(WdTags.WebIsEnabled, true);
        wdWidget.set(WdTags.WebType, "");
        wdWidget.set(WdTags.WebCssClasses, "[input]");
        wdWidget.set(Tags.Enabled, true);
        wdWidget.set(Tags.Blocked, false);

        WebdriverWidgetActionDeriver deriver = new WebdriverWidgetActionDeriver(ignoredWidget -> "100");
        SessionPolicyContext context = new SessionPolicyContext(
                new CompositeClickablePolicy(Collections.singletonList(new WebdriverClickablePolicy())),
                new CompositeTypeablePolicy(java.util.List.of(
                        new WebdriverTypeablePolicy(),
                        new ConfigurableWebdriverTypeableClassPolicy(Collections.singletonList("input"))
                )),
                new WebdriverScrollablePolicy(),
                new CompositeSelectablePolicy(Collections.singletonList(new WebdriverSelectablePolicy())),
                new CompositeEnabledPolicy(Collections.singletonList(widget -> true)),
                new CompositeBlockedPolicy(Collections.singletonList(widget -> false)),
                allowAllWidgets(),
                new CompositeVisiblePolicy(Collections.singletonList(widget -> true)),
                new CompositeAtCanvasPolicy(Collections.singletonList(widget -> true)),
                new CompositeTopLevelPolicy(Collections.singletonList(widget -> true))
        );
        Set<Action> actions = deriver.derive(null, wdWidget.root(), wdWidget, context);

        Assert.assertTrue(actions.stream().anyMatch(action ->
                String.valueOf(action.get(Tags.Role, null)).contains("RemoteScrollType")
                        && action.get(Tags.Desc, "").contains("input_amount")
        ));
    }

    @Test
    public void testDeriveAddsSelectListActionForSelectWidget() {
        WdWidgetStub wdWidget = new WdWidgetStub("country_select", "country", WdRoles.WdSELECT, "select");
        wdWidget.set(WdTags.WebTagName, "select");
        wdWidget.set(WdTags.WebId, "country");
        wdWidget.set(WdTags.WebInnerHTML, "<option value=\"nl\">Netherlands</option><option value=\"es\">Spain</option>");
        wdWidget.set(Tags.Enabled, true);
        wdWidget.set(Tags.Blocked, false);

        WebdriverWidgetActionDeriver deriver = new WebdriverWidgetActionDeriver(ignoredWidget -> "unused");
        SessionPolicyContext context = new SessionPolicyContext(
                new CompositeClickablePolicy(Collections.singletonList(new WebdriverClickablePolicy())),
                new CompositeTypeablePolicy(java.util.List.of(new WebdriverTypeablePolicy())),
                new WebdriverScrollablePolicy(),
                new CompositeSelectablePolicy(Collections.singletonList(new WebdriverSelectablePolicy())),
                new CompositeEnabledPolicy(Collections.singletonList(widget -> true)),
                new CompositeBlockedPolicy(Collections.singletonList(widget -> false)),
                allowAllWidgets(),
                new CompositeVisiblePolicy(Collections.singletonList(widget -> true)),
                new CompositeAtCanvasPolicy(Collections.singletonList(widget -> true)),
                new CompositeTopLevelPolicy(Collections.singletonList(widget -> true))
        );
        Set<Action> actions = deriver.derive(null, wdWidget.root(), wdWidget, context);

        Assert.assertTrue(actions.stream().anyMatch(action ->
                action instanceof WdSelectListAction
                        && action.get(Tags.Desc, "").contains("Netherlands")
                        && action.get(Tags.Desc, "").contains("Spain")
        ));
    }

    private WidgetFilterPolicy allowAllWidgets() {
        return widget -> true;
    }
}
