package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.WdDriver;

public abstract class SeleniumAction extends TaggableBase implements Action {

    protected final String target;
    protected final WebElement element;
    protected final Actions actions;

    public SeleniumAction(String target) {
        this.target = target;
        this.element = WdDriver.getRemoteWebDriver().findElement(By.xpath(target));
        this.actions = new Actions(WdDriver.getRemoteWebDriver());
    }

    public abstract String getType();

    public String getArgument() {
        return null;
    }

    protected abstract void performAction(State state);

    protected abstract Role getDefaultRole();

    protected abstract String getDescription();

    @Override
    public void run(SUT system, State state, double duration) {

        // System and state parameters not (yet) needed

        Util.pause(duration);
        performAction(state);
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
