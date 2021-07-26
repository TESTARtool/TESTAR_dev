package nl.ou.testar.StateModel.Selenium;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.openqa.selenium.WebElement;

public abstract class SeleniumAction extends TaggableBase implements Action {

    protected String target;

    public SeleniumAction(String target) {
        this.target = target;
    }

    public abstract String getType();

    public String getArgument() {
        return null;
    }

    protected abstract void performAction();

    protected abstract Role getDefaultRole();

    protected abstract String getDescription();

    @Override
    public void run(SUT system, State state, double duration) {

        // System and state parameters not (yet) needed

        Util.pause(duration);
        performAction();
    }

    @Override
    public String toShortString() {
        Role r = get(Tags.Role, null);
        if (r != null)
            return r.toString();
        else
            return toString();
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters){
            if (r.name().equals(getDefaultRole().name()))
                return getDescription();
        }
        return toString();
    }
}
