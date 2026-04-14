package org.testar.webdriver.policy;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestConfigurableWebdriverTypeableClassPolicy {

    @Test
    public void testNullWidgetIsNotTypeable() {
        ConfigurableWebdriverTypeableClassPolicy policy =
                new ConfigurableWebdriverTypeableClassPolicy(Collections.singletonList("input"));

        Assert.assertFalse(policy.isTypeable(null));
    }

    @Test
    public void testWidgetWithConfiguredTypeableClassIsTypeable() {
        ConfigurableWebdriverTypeableClassPolicy policy =
                new ConfigurableWebdriverTypeableClassPolicy(Collections.singletonList("input"));
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebCssClasses, "input");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testWidgetWithMultipleCssClassesMatchesConfiguredTypeableClass() {
        ConfigurableWebdriverTypeableClassPolicy policy =
                new ConfigurableWebdriverTypeableClassPolicy(Collections.singletonList("input"));
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebCssClasses, "[field,input,amount]");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testWidgetWithoutConfiguredTypeableClassIsNotTypeable() {
        ConfigurableWebdriverTypeableClassPolicy policy =
                new ConfigurableWebdriverTypeableClassPolicy(Collections.singletonList("input"));
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testEmptyConfigurationDoesNotMakeWidgetTypeable() {
        ConfigurableWebdriverTypeableClassPolicy policy =
                new ConfigurableWebdriverTypeableClassPolicy(Collections.emptyList());
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebCssClasses, "input");

        Assert.assertFalse(policy.isTypeable(widget));
    }

    private WidgetStub createRoleWidget(org.testar.core.alayer.Role role) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, role);
        return widget;
    }
}
