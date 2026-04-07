package org.testar.webdriver.action.policy;

import java.util.Collections;

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
    public void testDisabledWidgetIsNotClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputWidget("", false);

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testExplicitWebClickableWidgetIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebIsClickable, true);

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testInputWithSupportedClickableTypeIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputWidget("button", true);

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testInputWithoutSupportedClickableTypeIsNotClickableByDefault() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy();
        WidgetStub widget = createInputWidget("", true);

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testInputWithConfiguredClickableClassIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy(Collections.singletonList("clickable"));
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebCssClasses, "clickable");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testInputWithMultipleConfiguredClickableClassesIsClickable() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy(Collections.singletonList("clickable"));
        WidgetStub widget = createInputWidget("", true);
        widget.set(WdTags.WebCssClasses, "nav clickable highlighted");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testNonClickableRoleDoesNotBecomeClickableFromConfiguredClass() {
        WebdriverClickablePolicy policy = new WebdriverClickablePolicy(Collections.singletonList("clickable"));
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdDIV);
        widget.set(WdTags.WebIsEnabled, true);
        widget.set(WdTags.WebCssClasses, "clickable");

        Assert.assertFalse(policy.isClickable(widget));
    }

    private WidgetStub createInputWidget(String inputType, boolean enabled) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdINPUT);
        widget.set(WdTags.WebIsEnabled, enabled);
        widget.set(WdTags.WebType, inputType);
        return widget;
    }
}
