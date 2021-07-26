package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumActionFactory {
    public static final String CLEAR = "CLEAR";
    public static final String CLICK = "CLICK";
    public static final String KEYS = "KEYS";
    public static final String SUBMIT = "SUBMIT";

    public static SeleniumAction createAction(String type, String target, String argument, boolean replaceText) {
        if (CLEAR.equals(type)) {
            return new SeleniumClearAction(target);
        }
        if (CLICK.equals(type)) {
            return new SeleniumClickAction(target);
        }
        if (KEYS.equals(type)) {
            return new SeleniumSendKeysAction(target, argument, replaceText);
        }
        if (SUBMIT.equals(type)) {
            return new SeleniumSubmitAction();
        }
        return null;
    }
}
