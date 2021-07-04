package nl.ou.testar.StateModel.Selenium;

import org.openqa.selenium.WebElement;

public class SeleniumClickAction extends SeleniumAction {

    public SeleniumClickAction(WebElement target) {
        super(target);
    }

    @Override
    public String getType() {
        return "CLICK";
    }

    @Override
    public void run() {
        target.click();
    }
}
