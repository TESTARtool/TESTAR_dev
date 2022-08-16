package org.testar.monkey.alayer.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;

public class WdSecurityUrlInjectionAction extends TaggableBase implements Action {
    private String text;

    public WdSecurityUrlInjectionAction(String text)
    {
        super();
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.set(Tags.Desc, "Execute Webdriver script to redirect to different url");
        this.text = text;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException
    {
        System.out.println("New location");
        System.out.println(this.text);
        WdDriver.executeScript("window.location = \'"+this.text+"\'");
    }

    @Override
    public String toShortString() {
        return "Inject text in url";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}
