package org.testar.statemodel.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class SeleniumDragAndDropAction extends SeleniumAction {

    private final String destination;
    private final WebElement destinationElement;

    public SeleniumDragAndDropAction(String target, String destination) {
        super(target);
        this.destination = destination;
        this.destinationElement = WdDriver.getRemoteWebDriver().findElement(By.xpath(destination));
    }

    @Override
    public String toString() {
        return "Drag And Drop\t" + target + "\t" + destination;
    }

    @Override
    public String toParametersString() {
        return "(" + destination + ")";
    }

    @Override
    public String getType() {
        return SeleniumActionFactory.DRAG_DROP;
    }

    @Override
    protected void performAction(State state) {
        actions.dragAndDrop(element, destinationElement);
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumDrag;
    }

    @Override
    protected String getDescription() {
        return "Dragged and dropped to " + destination;
    }
}
