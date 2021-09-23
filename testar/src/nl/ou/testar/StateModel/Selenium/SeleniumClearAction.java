package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebElement;

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
        final WebElement element = WdDriver.getRemoteWebDriver().findElementByXPath(target);
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
