package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Action;
import org.fruit.alayer.Position;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.WidgetActionCompiler;
import org.fruit.alayer.webdriver.WdWidget;

public class SeleniumActionCompiler implements WidgetActionCompiler<WdWidget> {

    private final Action NOP = new NOP();

    @Override
    public Action leftClickAt(WdWidget w) {
        return new SeleniumClickAction(w.element.xPath);
    }

    @Override
    public Action leftDoubleClickAt(WdWidget w) {
        final SeleniumClickAction clickAction = new SeleniumClickAction(w.element.xPath);
        return new CompoundAction.Builder().add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 1).build();
    }

    @Override
    public Action leftTripleClickAt(WdWidget w) {
        final SeleniumClickAction clickAction = new SeleniumClickAction(w.element.xPath);
        return new CompoundAction.Builder().add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 1).build();
    }

    @Override
    public Action rightClickAt(WdWidget w) {
        // Not implemented
        throw new IllegalArgumentException("Right click action not applicable in Selenium");
    }

    @Override
    public Action dragFromTo(WdWidget from, WdWidget to) {
        // Not implemented
        throw new IllegalArgumentException("Drag action not applicable in Selenium");
    }

    @Override
    public Action clickTypeInto(WdWidget w, String text, boolean replaceText) {
        return new SeleniumSendKeysAction(w.element.xPath, text, replaceText);
    }

    @Override
    public Action pasteTextInto(WdWidget w, String text, boolean replaceText) {
        return new SeleniumSendKeysAction(w.element.xPath, text, replaceText);
    }
}
