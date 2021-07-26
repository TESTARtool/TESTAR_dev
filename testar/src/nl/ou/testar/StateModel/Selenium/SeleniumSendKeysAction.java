package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebElement;

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
    protected void performAction() {
        final WebElement element = WdDriver.getRemoteWebDriver().findElementByXPath(target);
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
