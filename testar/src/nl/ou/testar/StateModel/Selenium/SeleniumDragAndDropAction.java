package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebElement;

public class SeleniumDragAndDropAction extends SeleniumAction {

    private final String destination;
    private final WebElement destinationElement;

    public SeleniumDragAndDropAction(String target, String destination) {
        super(target);
        this.destination = destination;
        this.destinationElement = WdDriver.getRemoteWebDriver().findElementByXPath(destination);
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
