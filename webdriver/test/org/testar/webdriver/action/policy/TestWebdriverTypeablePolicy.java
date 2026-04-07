package org.testar.webdriver.action.policy;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverTypeablePolicy {

    @Test
    public void testNullWidgetIsNotTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();

        Assert.assertFalse(policy.isTypeable(null));
    }

    @Test
    public void testDisabledWidgetIsNotTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createInputWidget("", false);

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testInputWithSupportedTypeIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createInputWidget("text", true);

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testInputWithoutSupportedTypeIsNotTypeableByDefault() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createInputWidget("", true);

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testInputWithConfiguredTypeableClassIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy(Collections.singletonList("input"));
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebCssClasses, "input");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testInputWithMultipleConfiguredTypeableClassesIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy(Collections.singletonList("input"));
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebCssClasses, "field input amount");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testInputWithStateFormattedCssClassesIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy(Collections.singletonList("input"));
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebCssClasses, "[input]");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testNonTypeableRoleDoesNotBecomeTypeableFromConfiguredClass() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy(Collections.singletonList("input"));
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdDIV);
        widget.set(WdTags.WebIsEnabled, true);
        widget.set(WdTags.WebCssClasses, "input");

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testTextareaIsTypeableWithoutInputType() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdTEXTAREA);
        widget.set(WdTags.WebIsEnabled, true);

        Assert.assertTrue(policy.isTypeable(widget));
    }

    private WidgetStub createInputWidget(String inputType, boolean enabled) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdINPUT);
        widget.set(WdTags.WebIsEnabled, enabled);
        widget.set(WdTags.WebType, inputType);
        return widget;
    }
}
