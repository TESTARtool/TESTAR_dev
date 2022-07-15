package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class SeleniumClearAction extends SeleniumAction {
    public SeleniumClearAction(String target) {
        super(target);
    }

    @Override
    public String getType() {
        return "CLEAR";
    }

    @Override
    protected void performAction(State state) {
        final WebElement element = WdDriver.getRemoteWebDriver().findElement(By.xpath(target));
        element.clear();
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumClear;
    }

    @Override
    protected String getDescription() {
        return "Item cleared";
    }

    @Override
    public String toString() {
        return "Clear Element Text\t" + target;
    }

    @Override
    public String toParametersString() {
        return "";
    }
}
