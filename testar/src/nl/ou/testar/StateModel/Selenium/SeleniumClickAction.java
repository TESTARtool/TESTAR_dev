package nl.ou.testar.StateModel.Selenium;

import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebElement;

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
        final WebElement element = WdDriver.getRemoteWebDriver().findElementByXPath(target);
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
