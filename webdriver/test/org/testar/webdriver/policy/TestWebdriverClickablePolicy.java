package org.testar.webdriver.policy;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverClickablePolicy {

    @Test
    public void testNullWidgetIsNotClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();

        Assert.assertFalse(policy.isClickable(null));
    }

    @Test
    public void testInputRoleWithoutClickableTypeIsNotClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputRoleWidget("");

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testExplicitlyClickableWidgetIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputRoleWidget("");
        widget.set(WdTags.WebIsClickable, true);

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testInputRoleWithClickableTypeIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputRoleWidget("button");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testButtonRoleIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdBUTTON);

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testAnchorRoleIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdA);

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testCustomRoleWithoutNativeOrExplicitClickableSignalIsNotClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdSPAN);

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testAriaButtonRoleIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebAriaRole, "button");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testAriaMenuItemRoleIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebAriaRole, "menuitem");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testCustomRoleWithClickableTypeIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createRoleWidget(WdRoles.WdDIV);
        widget.set(WdTags.WebType, "radio");

        Assert.assertTrue(policy.isClickable(widget));
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
