package nl.ou.testar.StateModel.Selenium;

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
    public void run() {
        target.sendKeys(argument);
    }
}
