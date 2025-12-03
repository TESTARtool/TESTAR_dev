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
public class TestAndroidIsClickable {

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
        Role[] roles = AndroidRoles.nativeClickableRoles();
        java.util.List<Object[]> params = new java.util.ArrayList<>(roles.length);
        for (Role r : roles) {
            params.add(new Object[]{r});
        }
        return params;
    }

    @Parameterized.Parameter
    public Role role;

    private WidgetStub createWidget(Role role, boolean clickable, boolean enabled) {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(Tags.Role, role);
        widget.set(AndroidTags.AndroidClickable, clickable);
        widget.set(AndroidTags.AndroidEnabled, enabled);

        return widget;
    }

    @Test
    public void isClickableRole() {
        WidgetStub widget = createWidget(role, true, true);
        assertTrue("Expected clickable for role: " + role, androidProtocol.isClickable(widget));
    }

    @Test
    public void notClickableRoleWhenClickableFalse() {
        WidgetStub widget = createWidget(role, false, true);
        assertFalse("Expected NOT clickable when AndroidClickable=false for role: " + role,
                androidProtocol.isClickable(widget));
    }

    @Test
    public void notClickableRoleWhenDisabled() {
        WidgetStub widget = createWidget(role, true, false);
        assertFalse("Expected NOT clickable when AndroidEnabled=false for role: " + role,
                androidProtocol.isClickable(widget));
    }

    @Test
    public void notClickableRoleWhenClickableFalseAndDisabled() {
        WidgetStub widget = createWidget(role, false, false);
        assertFalse("Expected NOT clickable when AndroidClickable=false & AndroidEnabled=false for role: " + role,
                androidProtocol.isClickable(widget));
    }

    @Test
    public void roleIsNotClickable() {
        WidgetStub widget = createWidget(AndroidRoles.AndroidEditText, true, true);
        assertFalse("Expected NOT clickable for role: " + AndroidRoles.AndroidEditText, androidProtocol.isClickable(widget));
    }

}
