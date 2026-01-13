package org.testar.protocols;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.plugin.NativeLinker;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

@RunWith(Parameterized.class)
public class TestAndroidIsTypeable {

    private static AndroidProtocol androidProtocol;

    @BeforeClass
    public static void setup() {
        // To avoid issues with java awt robot, we only execute this unit tests in windows environments.
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
        NativeLinker.addAndroidOS();
        androidProtocol = new AndroidProtocol();
    }

    @AfterClass
    public static void clean() {
        NativeLinker.cleanAndroidOS();
    }

    @Parameterized.Parameters(name = "{index}: role={0}")
    public static Collection<Object[]> data() {
        Role[] roles = AndroidRoles.nativeTypeableRoles();
        java.util.List<Object[]> params = new java.util.ArrayList<>(roles.length);
        for (Role r : roles) {
            params.add(new Object[]{r});
        }
        return params;
    }

    @Parameterized.Parameter
    public Role role;

    private WidgetStub createWidget(Role role, boolean focusable, boolean enabled, boolean displayed) {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(Tags.Role, role);
        widget.set(AndroidTags.AndroidFocusable, focusable);
        widget.set(AndroidTags.AndroidEnabled, enabled);
        widget.set(AndroidTags.AndroidDisplayed, displayed);

        return widget;
    }

    @Test
    public void isTypeableRole() {
        WidgetStub widget = createWidget(role, true, true, true);
        assertTrue("Expected typeable for role: " + role, androidProtocol.isTypeable(widget));
    }

    @Test
    public void notTypeableRoleWhenFocusableFalse() {
        WidgetStub widget = createWidget(role, false, true, true);
        assertFalse("Expected NOT typeable when AndroidFocusable=false for role: " + role,
                androidProtocol.isTypeable(widget));
    }

    @Test
    public void notTypeableRoleWhenDisabled() {
        WidgetStub widget = createWidget(role, true, false, true);
        assertFalse("Expected NOT typeable when AndroidEnabled=false for role: " + role,
                androidProtocol.isTypeable(widget));
    }

    @Test
    public void notTypeableRoleWhenNotDisplayed() {
        WidgetStub widget = createWidget(role, true, true, false);
        assertFalse("Expected NOT typeable when AndroidDisplayed=false for role: " + role,
                androidProtocol.isTypeable(widget));
    }

    @Test
    public void notTypeableRoleWhenFocusableFalseAndDisabled() {
        WidgetStub widget = createWidget(role, false, false, true);
        assertFalse("Expected NOT typeable when AndroidFocusable=false & AndroidEnabled=false for role: " + role,
                androidProtocol.isTypeable(widget));
    }

    @Test
    public void notTypeableRoleWhenFocusableFalseDisabledAndNotDisplayed() {
        WidgetStub widget = createWidget(role, false, false, false);
        assertFalse("Expected NOT typeable when AndroidFocusable=false, AndroidEnabled=false & AndroidDisplayed=false for role: " + role,
                androidProtocol.isTypeable(widget));
    }

    @Test
    public void roleIsNotTypeable() {
        WidgetStub widget = createWidget(AndroidRoles.AndroidButton, true, true, true);
        assertFalse("Expected NOT typeable for role: " + AndroidRoles.AndroidButton, androidProtocol.isTypeable(widget));
    }

}
