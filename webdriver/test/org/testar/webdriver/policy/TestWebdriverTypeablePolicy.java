package org.testar.webdriver.policy;

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
    public void testInputWithoutTypeIsNotTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createInputRoleWidget("");

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testInputRoleWithTextTypeIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createInputRoleWidget("text");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testCustomRoleWithoutNativeTypeableSignalIsNotTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testTextareaRoleIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdTEXTAREA);

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testCustomRoleWithTypeableTypeIsTypeable() {
        WebdriverTypeablePolicy policy = new WebdriverTypeablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebType, "text");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    private WidgetStub createInputRoleWidget(String inputType) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdINPUT);
        widget.set(WdTags.WebType, inputType);
        return widget;
    }

    private WidgetStub createRoleWidget(org.testar.core.alayer.Role role) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, role);
        return widget;
    }
}
