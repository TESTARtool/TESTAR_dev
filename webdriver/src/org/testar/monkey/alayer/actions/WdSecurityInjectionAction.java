package org.testar.monkey.alayer.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.webdriver.WdWidget;

public class WdSecurityInjectionAction extends TaggableBase implements Action {
    private WebElement element;
    private String text;

    public WdSecurityInjectionAction(RemoteWebDriver webDriver, WdWidget widget, String text)
    {
        element = webDriver.findElement(new By.ById(widget.element.id));
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.set(Tags.Desc, "Inject text that contains special characters");
        this.text = text;
        this.set(Tags.OriginWidget, widget);
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        element.clear();
        element.sendKeys(this.text);
    }

    @Override
    public String toShortString() {
        return "Inject text";
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
