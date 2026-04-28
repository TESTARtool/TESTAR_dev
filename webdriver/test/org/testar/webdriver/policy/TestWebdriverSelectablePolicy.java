package org.testar.webdriver.policy;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebdriverSelectablePolicy {

    @Test
    public void testNullWidgetIsNotSelectable() {
        WebdriverSelectablePolicy policy = new WebdriverSelectablePolicy();

        Assert.assertFalse(policy.isSelectable(null));
    }

    @Test
    public void testNativeSelectIsSelectable() {
        WebdriverSelectablePolicy policy = new WebdriverSelectablePolicy();
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdSELECT);
        widget.set(WdTags.WebTagName, "select");

        Assert.assertTrue(policy.isSelectable(widget));
    }

    @Test
    public void testNonSelectTagIsNotSelectable() {
        WebdriverSelectablePolicy policy = new WebdriverSelectablePolicy();
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, WdRoles.WdDIV);
        widget.set(WdTags.WebTagName, "div");

        Assert.assertFalse(policy.isSelectable(widget));
    }
}
