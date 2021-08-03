package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.State;
import org.fruit.alayer.webdriver.WdDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumActionFactory {
    public static final String CLEAR = "CLEAR";
    public static final String CLICK = "CLICK";
    public static final String DRAG_DROP = "DRAG_DROP";
    public static final String DRAG_TO = "DRAG_TO";
    public static final String KEYS = "KEYS";
    public static final String SUBMIT = "SUBMIT";

    public static SeleniumAction createAction(State state, String type, String target, boolean replaceText, String... args) {
        if (CLEAR.equals(type)) {
            return new SeleniumClearAction(target);
        }
        if (CLICK.equals(type)) {
            return new SeleniumClickAction(target);
        }
        if (DRAG_DROP.equals(type)) {
            return new SeleniumDragAndDropAction(target, args[0]);
        }
        // TODO
//        if (DRAG_TO.equals(type)) {
//            final String params[] = argument.split(",");
//            int xOffset = 0, yOffset = 0;
//            if (params.length >= 2) {
//                xOffset = Integer.parseInt(params[0]);
//                yOffset = Integer.parseInt(params[1]);
//            }
//            return new SeleniumDragToAction(target, xOffset, yOffset);
//        }
        if (KEYS.equals(type)) {
            return new SeleniumSendKeysAction(target, args[0], replaceText);
        }
        if (SUBMIT.equals(type)) {
            return new SeleniumSubmitAction();
        }
        return null;
    }
}
