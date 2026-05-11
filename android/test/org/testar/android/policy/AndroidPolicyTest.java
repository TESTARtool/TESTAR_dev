package org.testar.android.policy;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;

public final class AndroidPolicyTest {

    @Test
    public void clickablePolicyAcceptsClickableDisplayedEnabledNativeRole() {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidButton);
        widget.set(AndroidTags.AndroidClickable, true);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        Assert.assertTrue(new AndroidClickablePolicy().isClickable(widget));
    }

    @Test
    public void clickablePolicyRejectsNonNativeRole() {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidEditText);
        widget.set(AndroidTags.AndroidClickable, true);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        Assert.assertFalse(new AndroidClickablePolicy().isClickable(widget));
    }

    @Test
    public void typeablePolicyAcceptsFocusableDisplayedEnabledNativeRole() {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidEditText);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidFocusable, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        Assert.assertTrue(new AndroidTypeablePolicy().isTypeable(widget));
    }

    @Test
    public void typeablePolicyRejectsNonTypeableRole() {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidButton);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidFocusable, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        Assert.assertFalse(new AndroidTypeablePolicy().isTypeable(widget));
    }

    @Test
    public void scrollablePolicyUsesAndroidScrollableTag() {
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidScrollable, true);

        Assert.assertTrue(new AndroidScrollablePolicy().isScrollable(widget));
        widget.set(AndroidTags.AndroidScrollable, false);
        Assert.assertFalse(new AndroidScrollablePolicy().isScrollable(widget));
    }
}
