package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class SeleniumSubmitAction extends SeleniumAction {

    public SeleniumSubmitAction() {
        super(null);
    }

    @Override
    public String getType() {
        return "SUBMIT";
    }

    @Override
    protected void performAction(State state) {
        final WebElement element = WdDriver.getRemoteWebDriver().findElement(By.xpath(target));
        element.submit();
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumSubmit;
    }

    @Override
    protected String getDescription() {
        return "Form submitted";
    }

    @Override
    public String toParametersString() {
        return "";
    }
}
