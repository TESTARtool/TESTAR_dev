package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class SeleniumClickAction extends SeleniumAction {

    public SeleniumClickAction(String target) {
        super(target);
    }

    @Override
    public String getType() {
        return "CLICK";
    }

    @Override
    protected void performAction(State state) {
        final WebElement element = WdDriver.getRemoteWebDriver().findElement(By.xpath(target));
        element.click();
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumClick;
    }

    @Override
    public String toString() {
        return "Click Element\t" + target;
    }

    @Override
    protected String getDescription() {
        return "Item clicked";
    }

    @Override
    public String toParametersString() {
        return "";
    }
}
