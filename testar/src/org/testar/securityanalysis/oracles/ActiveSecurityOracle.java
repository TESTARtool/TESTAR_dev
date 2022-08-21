package org.testar.securityanalysis.oracles;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public abstract class ActiveSecurityOracle extends BaseSecurityOracle {
    protected RemoteWebDriver webDriver;

    public ActiveSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver){
        super(securityResultWriter);
        this.webDriver = webDriver;
    }

    /** Enables oracle to add actions to action pool **/
    public Set<Action> getActions(State state)
    {
        /** Add actions **/
        return new HashSet<>();
    }

    /** Enables oracle to select action **/
    public Set<Action> preSelect(Set<Action> actions)
    {
        return actions;
    }

    /** Lets the oracle know what action is executed for potential followup actions **/
    public void actionSelected(Action action)
    {
    }

    //region helpers
    protected boolean isAtBrowserCanvas(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return false;
        }

        // Widget must be completely visible on viewport for screenshots
        return widget.get(WdTags.WebIsFullOnScreen, false);
    }

    protected boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
            // Input type are special...
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.typeableInputTypes().contains(type.toLowerCase());
            }
            return true;
        }

        return false;
    }
    //endregion
}
