package org.testar.webdriver.action;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.core.tag.Tags;
import org.testar.engine.action.derivation.ActionDerivationContext;
import org.testar.webdriver.action.policy.WebdriverClickablePolicy;
import org.testar.webdriver.action.policy.WebdriverScrollablePolicy;
import org.testar.webdriver.action.policy.WebdriverTypeablePolicy;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.stub.WdWidgetStub;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverWidgetActionDeriver {

    @Test
    public void testDeriveAddsTypeActionForInputWithConfiguredTypeableClass() {
        WdWidgetStub widget = new WdWidgetStub("input_amount", "amount", WdRoles.WdINPUT, "input");
        widget.set(WdTags.WebIsEnabled, true);
        widget.set(WdTags.WebType, "");
        widget.set(WdTags.WebCssClasses, "[input]");
        widget.set(Tags.Enabled, true);
        widget.set(Tags.Blocked, false);

        WebdriverWidgetActionDeriver deriver = new WebdriverWidgetActionDeriver(ignoredWidget -> "100");
        ActionDerivationContext context = new ActionDerivationContext(
                new WebdriverClickablePolicy(),
                new WebdriverTypeablePolicy(Collections.singletonList("input")),
                new WebdriverScrollablePolicy(),
                allowAllWidgets()
        );
        Set<Action> actions = new LinkedHashSet<>();

        deriver.derive(null, widget.root(), widget, context, actions);

        Assert.assertTrue(actions.stream().anyMatch(action ->
                String.valueOf(action.get(Tags.Role, null)).contains("RemoteScrollType")
                        && action.get(Tags.Desc, "").contains("input_amount")
        ));
    }

    private WidgetFilterPolicy allowAllWidgets() {
        return widget -> true;
    }
}
