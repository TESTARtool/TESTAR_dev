package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;
import org.openqa.selenium.WebElement;

public class SeleniumSubmitAction extends SeleniumAction {

    public SeleniumSubmitAction() {
        super(null);
    }

    @Override
    public String getType() {
        return "SUBMIT";
    }

    @Override
    protected void performAction() {
        target.submit();
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
