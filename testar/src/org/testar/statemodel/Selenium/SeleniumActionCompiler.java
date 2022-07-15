package org.testar.statemodel.Selenium;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Position;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.WidgetActionCompiler;
import org.testar.monkey.alayer.webdriver.WdWidget;

public class SeleniumActionCompiler implements WidgetActionCompiler<WdWidget> {

    private final State state;
    private final Action NOP = new NOP();

    public SeleniumActionCompiler(State state) {
        this.state = state;
    }

    @Override
    public Action leftClickAt(WdWidget w) {
        return new SeleniumClickAction(w.element.xPath);
    }

    @Override
    public Action leftDoubleClickAt(WdWidget w) {
        final SeleniumClickAction clickAction = new SeleniumClickAction(w.element.xPath);
        clickAction.set(Tags.Role, ActionRoles.SeleniumClick);
        return new CompoundAction.Builder().add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 1).build();
    }

    @Override
    public Action leftTripleClickAt(WdWidget w) {
        final SeleniumClickAction clickAction = new SeleniumClickAction(w.element.xPath);
        clickAction.set(Tags.Role, ActionRoles.SeleniumClick);
        return new CompoundAction.Builder().add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 0.1).
                add(clickAction, 0).add(NOP, 1).build();
    }

    @Override
    public Action dragFromTo(WdWidget from, WdWidget to) {
        final SeleniumDragAndDropAction dragAndDropAction = new SeleniumDragAndDropAction(from.element.xPath, to.element.xPath);
        dragAndDropAction.set(Tags.Role, ActionRoles.SeleniumDrag);
        return dragAndDropAction;
    }

    @Override
    public Action slideFromTo(Position from, Position to, WdWidget widget) {
        final SeleniumDragToAction dragToAction = new SeleniumDragToAction(widget.element.xPath, state, from, to);
        dragToAction.set(Tags.Role, ActionRoles.SeleniumDrag);
        return dragToAction;
    }

    @Override
    public Action clickTypeInto(WdWidget w, String text, boolean replaceText) {
        final SeleniumSendKeysAction sendKeysAction = new SeleniumSendKeysAction(w.element.xPath, text, replaceText);
        sendKeysAction.set(Tags.Role, ActionRoles.SeleniumSendKeys);
        return sendKeysAction;
    }

    @Override
    public Action pasteTextInto(WdWidget w, String text, boolean replaceText) {
        final SeleniumSendKeysAction sendKeysAction = new SeleniumSendKeysAction(w.element.xPath, text, replaceText);
        sendKeysAction.set(Tags.Role, ActionRoles.SeleniumSendKeys);
        return sendKeysAction;
    }
}
