package org.testar.webdriver.action;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.webdriver.state.WdElement;
import org.testar.webdriver.state.WdRootElement;
import org.testar.webdriver.state.WdState;
import org.testar.webdriver.state.WdWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WebdriverActionIdentityTest {

    private WdState state;
    private WdWidget widget;

    @Before
    public void prepareIdentifiedStateAndWidget() {
        WdRootElement rootElement = new WdRootElement();
        state = new WdState(rootElement);
        WdElement element = new WdElement(rootElement, rootElement);
        element.rect = Rect.from(0, 0, 100, 40);
        widget = new WdWidget(state, state, element);
        state.set(Tags.AbstractID, "state-abstract");
        state.set(Tags.ConcreteID, "state-concrete");
        widget.set(Tags.AbstractID, "widget-abstract");
        widget.set(Tags.ConcreteID, "widget-concrete");
    }

    @Test
    public void selectionsRemainDistinctAbstractActionsIncludingCaseSensitiveValues() {
        Action lower = new WdSelectListAction("cars", "saab", widget, WdSelectListAction.JsTargetMethod.ID);
        Action upper = new WdSelectListAction("cars", "Saab", widget, WdSelectListAction.JsTargetMethod.ID);
        Action equivalent = new WdSelectListAction("cars", "saab", widget, WdSelectListAction.JsTargetMethod.ID);
        Action byName = new WdSelectListAction("cars", "saab", widget, WdSelectListAction.JsTargetMethod.NAME);
        CodingManager.buildIDs(state, Set.of(lower, upper, equivalent, byName));

        assertEquals(lower.get(Tags.AbstractID), equivalent.get(Tags.AbstractID));
        assertEquals(lower.get(Tags.ConcreteID), equivalent.get(Tags.ConcreteID));
        assertNotEquals(lower.get(Tags.AbstractID), upper.get(Tags.AbstractID));
        assertNotEquals(lower.get(Tags.ConcreteID), upper.get(Tags.ConcreteID));
        assertEquals(lower.get(Tags.AbstractID), byName.get(Tags.AbstractID));
        assertNotEquals(lower.get(Tags.ConcreteID), byName.get(Tags.ConcreteID));
    }

    @Test
    public void remoteTypingUsesFullTextOnlyForConcreteIdentity() {
        Action first = new WdRemoteTypeAction(widget, "same-long-prefix-first");
        Action second = new WdRemoteTypeAction(widget, "same-long-prefix-second");
        Action scrollFirst = new WdRemoteScrollTypeAction(widget, "same-long-prefix-first");
        Action scrollSecond = new WdRemoteScrollTypeAction(widget, "same-long-prefix-second");
        CodingManager.buildIDs(state, Set.of(first, second, scrollFirst, scrollSecond));

        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));
        assertEquals(scrollFirst.get(Tags.AbstractID), scrollSecond.get(Tags.AbstractID));
        assertNotEquals(scrollFirst.get(Tags.ConcreteID), scrollSecond.get(Tags.ConcreteID));
        assertNotEquals(first.get(Tags.AbstractID), scrollFirst.get(Tags.AbstractID));
    }

    @Test
    public void remoteClickDoesNotDependOnDisplayDescriptions() {
        Action action = new WdRemoteClickAction(widget);
        CodingManager.buildIDs(state, Collections.singleton(action));
        String abstractId = action.get(Tags.AbstractID);
        String concreteId = action.get(Tags.ConcreteID);

        widget.set(Tags.Desc, "changed display description");
        Action equivalent = new WdRemoteClickAction(widget);
        Action scrollClick = new WdRemoteScrollClickAction(widget);
        CodingManager.buildIDs(state, Set.of(equivalent, scrollClick));
        assertEquals(abstractId, equivalent.get(Tags.AbstractID));
        assertEquals(concreteId, equivalent.get(Tags.ConcreteID));
        assertNotEquals(abstractId, scrollClick.get(Tags.AbstractID));
    }

    @Test
    public void navigationAttributeAndSubmitTargetsDistinguishOriginlessActions() {
        Action firstUrl = new WdSecurityUrlInjectionAction("https://example.org/first");
        Action secondUrl = new WdSecurityUrlInjectionAction("https://example.org/second");
        Action firstAttribute = new WdAttributeAction("field", "disabled", "true");
        Action secondAttribute = new WdAttributeAction("field", "disabled", "false");
        Action firstSubmit = new WdSubmitAction("first-form");
        Action secondSubmit = new WdSubmitAction("second-form");
        CodingManager.buildIDs(state, Set.of(firstUrl, secondUrl, firstAttribute, secondAttribute, firstSubmit, secondSubmit));

        assertNotEquals(firstUrl.get(Tags.AbstractID), secondUrl.get(Tags.AbstractID));
        assertNotEquals(firstUrl.get(Tags.ConcreteID), secondUrl.get(Tags.ConcreteID));
        assertNotEquals(firstAttribute.get(Tags.AbstractID), secondAttribute.get(Tags.AbstractID));
        assertNotEquals(firstAttribute.get(Tags.ConcreteID), secondAttribute.get(Tags.ConcreteID));
        assertNotEquals(firstSubmit.get(Tags.AbstractID), secondSubmit.get(Tags.AbstractID));
        assertNotEquals(firstSubmit.get(Tags.ConcreteID), secondSubmit.get(Tags.ConcreteID));
    }
}
