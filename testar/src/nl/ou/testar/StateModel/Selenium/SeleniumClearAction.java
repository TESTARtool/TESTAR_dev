package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;
import org.openqa.selenium.WebElement;

public class SeleniumClearAction extends SeleniumAction {
    public SeleniumClearAction(WebElement target) {
        super(target);
    }

    @Override
    public String getType() {
        return "CLEAR";
    }

    @Override
    protected void performAction() {
        target.clear();
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
        return "Clear Element Text\t" + target.getAttribute("xpath");
    }

    @Override
    public String toParametersString() {
        return "";
    }
}
