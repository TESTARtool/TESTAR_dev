package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;
import org.openqa.selenium.WebElement;

public class SeleniumSendKeysAction extends SeleniumAction {

    private CharSequence argument;

    public SeleniumSendKeysAction(WebElement target, CharSequence argument) {
        super(target);
        this.argument = argument;
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
        target.sendKeys(argument);
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
        return "Input Text\t" + target.getAttribute("xpath") + "\t" + argument + "\nFalse";
    }

    @Override
    public String toParametersString() {
        return "(" + argument + ")";
    }
}
