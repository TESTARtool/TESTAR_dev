package org.testar.webdriver.policy;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestConfigurableWebdriverClickableClassPolicy {

    @Test
    public void testNullWidgetIsNotClickable() {
        ConfigurableWebdriverClickableClassPolicy policy =
                new ConfigurableWebdriverClickableClassPolicy(Collections.singletonList("clickable"));

        Assert.assertFalse(policy.isClickable(null));
    }

    @Test
    public void testWidgetWithConfiguredClickableClassIsClickable() {
        ConfigurableWebdriverClickableClassPolicy policy =
                new ConfigurableWebdriverClickableClassPolicy(Collections.singletonList("clickable"));
        WidgetStub widget = createRoleWidget(WdRoles.WdSPAN);
        widget.set(WdTags.WebCssClasses, "clickable");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testWidgetWithMultipleCssClassesMatchesConfiguredClickableClass() {
        ConfigurableWebdriverClickableClassPolicy policy =
                new ConfigurableWebdriverClickableClassPolicy(Collections.singletonList("clickable"));
        WidgetStub widget = createRoleWidget(WdRoles.WdSPAN);
        widget.set(WdTags.WebCssClasses, "[nav,clickable,highlighted]");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testWidgetWithoutConfiguredClickableClassIsNotClickable() {
        ConfigurableWebdriverClickableClassPolicy policy =
                new ConfigurableWebdriverClickableClassPolicy(Collections.singletonList("clickable"));
        WidgetStub widget = createRoleWidget(WdRoles.WdSPAN);

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testEmptyConfigurationDoesNotMakeWidgetClickable() {
        ConfigurableWebdriverClickableClassPolicy policy =
                new ConfigurableWebdriverClickableClassPolicy(Collections.emptyList());
        WidgetStub widget = createRoleWidget(WdRoles.WdSPAN);
        widget.set(WdTags.WebCssClasses, "clickable");

        Assert.assertFalse(policy.isClickable(widget));
    }

    private WidgetStub createRoleWidget(org.testar.core.alayer.Role role) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, role);
        return widget;
    }
}
