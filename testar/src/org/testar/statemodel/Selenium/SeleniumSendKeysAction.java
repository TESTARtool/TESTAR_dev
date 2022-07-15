package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class SeleniumSendKeysAction extends SeleniumAction {

    private CharSequence argument;
    private boolean replaceText;

    public SeleniumSendKeysAction(String target, CharSequence argument, boolean replaceText) {
        super(target);
        this.argument = argument;
        this.replaceText = replaceText;
    }

    @Override
    public String getType() {
        return "KEYS";
    }

    @Override
    public String getArgument() {
        return argument.toString();
    }

    @Override
    protected void performAction(State state) {
        final WebElement element = WdDriver.getRemoteWebDriver().findElement(By.xpath(target));
        if (replaceText) {
            element.clear();
        }
        element.sendKeys(argument);
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumSendKeys;
    }

    @Override
    protected String getDescription() {
        return "String value inserted: " + argument;
    }

    @Override
    public String toString() {
        return "Input Text\t" + target + "\t" + argument + "\nFalse";
    }

    @Override
    public String toParametersString() {
        return "(" + argument + ")";
    }
}
