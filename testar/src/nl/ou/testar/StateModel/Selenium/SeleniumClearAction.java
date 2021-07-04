package nl.ou.testar.StateModel.Selenium;

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
    public void run() {
        target.clear();
    }
}
