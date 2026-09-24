package org.testar.windows.action.policy;

import org.junit.Test;
import org.testar.core.alayer.Role;
import org.testar.core.tag.Tags;
import org.testar.stub.WidgetStub;
import org.testar.windows.alayer.UIARoles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class WindowsClickablePolicyTest {

    @Test
    public void acceptsEveryConfiguredClickableRole() {
        WindowsClickablePolicy policy = new WindowsClickablePolicy();

        for (Role role : WindowsClickablePolicy.getClickableRoles()) {
            WidgetStub widget = widgetWithRole(role);
            assertTrue("Expected clickable for role: " + role, policy.isClickable(widget));
        }
    }

    @Test
    public void rejectsRepresentativeNonClickableRoles() {
        WindowsClickablePolicy policy = new WindowsClickablePolicy();

        assertFalse(policy.isClickable(widgetWithRole(UIARoles.UIAMenu)));
        assertFalse(policy.isClickable(widgetWithRole(UIARoles.UIAList)));
        assertFalse(policy.isClickable(widgetWithRole(UIARoles.UIAEdit)));
        assertFalse(policy.isClickable(widgetWithRole(UIARoles.UIAWindow)));
    }

    private static WidgetStub widgetWithRole(Role role) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, role);
        return widget;
    }
}
