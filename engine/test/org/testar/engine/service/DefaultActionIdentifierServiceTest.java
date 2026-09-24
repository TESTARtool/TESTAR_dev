package org.testar.engine.service;

import java.util.Collections;

import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.action.ActivateSystem;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public final class DefaultActionIdentifierServiceTest {

    @Test
    public void identifiesWidgetActionWhenOriginWidgetHasPath() {
        StateStub state = stateWithWidget();
        WidgetStub widget = (WidgetStub) state.child(0);
        Action action = new AnnotatingActionCompiler().leftClickAt(widget);

        new DefaultActionIdentifierService().identifyActions(state, Collections.singleton(action));

        assertSame(widget, action.get(Tags.OriginWidget, null));
        assertNotNull(action.get(Tags.AbstractID, null));
        assertNotNull(action.get(Tags.ConcreteID, null));
    }

    @Test
    public void identifiesEnvironmentActionWithoutOriginWidget() {
        State state = new StateStub();
        CodingManager.buildIDs(state);
        Action action = new ActivateSystem();

        new DefaultActionIdentifierService().identifyEnvironmentAction(state, action);

        assertNotNull(action.get(Tags.AbstractID, null));
        assertNotNull(action.get(Tags.ConcreteID, null));
    }

    private StateStub stateWithWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.set(Tags.Role, Roles.Button);
        widget.set(Tags.Path, "[0,0,1]");
        widget.set(Tags.Shape, Rect.from(0, 0, 100, 40));
        state.addChild(widget);
        CodingManager.buildIDs(state);
        return state;
    }
}
