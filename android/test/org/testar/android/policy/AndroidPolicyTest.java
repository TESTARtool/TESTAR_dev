package org.testar.android.policy;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.Role;
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
    public void clickablePolicyAcceptsEveryNativeClickableRole() {
        AndroidClickablePolicy policy = new AndroidClickablePolicy();

        for (Role role : AndroidRoles.nativeClickableRoles()) {
            WidgetStub widget = new WidgetStub();
            widget.set(Tags.Role, role);
            widget.set(AndroidTags.AndroidClickable, true);
            widget.set(AndroidTags.AndroidEnabled, true);
            widget.set(AndroidTags.AndroidDisplayed, true);

            Assert.assertTrue("Expected clickable for role: " + role, policy.isClickable(widget));
        }
    }

    @Test
    public void clickablePolicyRequiresAllAndroidStateGates() {
        AndroidClickablePolicy policy = new AndroidClickablePolicy();
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidButton);

        widget.set(AndroidTags.AndroidClickable, false);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, true);
        Assert.assertFalse(policy.isClickable(widget));

        widget.set(AndroidTags.AndroidClickable, true);
        widget.set(AndroidTags.AndroidEnabled, false);
        Assert.assertFalse(policy.isClickable(widget));

        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, false);
        Assert.assertFalse(policy.isClickable(widget));
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
    public void typeablePolicyAcceptsEveryNativeTypeableRole() {
        AndroidTypeablePolicy policy = new AndroidTypeablePolicy();

        for (Role role : AndroidRoles.nativeTypeableRoles()) {
            WidgetStub widget = new WidgetStub();
            widget.set(Tags.Role, role);
            widget.set(AndroidTags.AndroidFocusable, true);
            widget.set(AndroidTags.AndroidEnabled, true);
            widget.set(AndroidTags.AndroidDisplayed, true);

            Assert.assertTrue("Expected typeable for role: " + role, policy.isTypeable(widget));
        }
    }

    @Test
    public void typeablePolicyRequiresAllAndroidStateGates() {
        AndroidTypeablePolicy policy = new AndroidTypeablePolicy();
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, AndroidRoles.AndroidEditText);

        widget.set(AndroidTags.AndroidFocusable, false);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, true);
        Assert.assertFalse(policy.isTypeable(widget));

        widget.set(AndroidTags.AndroidFocusable, true);
        widget.set(AndroidTags.AndroidEnabled, false);
        Assert.assertFalse(policy.isTypeable(widget));

        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, false);
        Assert.assertFalse(policy.isTypeable(widget));
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
