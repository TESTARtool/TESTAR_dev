package nl.ou.testar.StateModel.Selenium;

import org.openqa.selenium.WebElement;

public abstract class SeleniumAction {

    protected WebElement target;

    public SeleniumAction(WebElement target) {
        this.target = target;
    }

    public abstract String getType();

    public String getArgument() {
        return null;
    }

    public abstract void run();
}
