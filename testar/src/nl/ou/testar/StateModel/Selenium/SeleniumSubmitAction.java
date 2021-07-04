package nl.ou.testar.StateModel.Selenium;

import org.openqa.selenium.WebElement;

public class SeleniumSubmitAction extends SeleniumAction {

    public SeleniumSubmitAction(WebElement target) {
        super(target);
    }

    @Override
    public String getType() {
        return "SUBMIT";
    }

    @Override
    public void run() {
        target.submit();
    }
}
