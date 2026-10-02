package org.testar.engine.service;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.action.ActivateSystem;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.Type;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

    @Test
    public void identifiesMixedActionSetWithoutRequiringWidgetPaths() {
        StateStub state = new StateStub();
        state.set(Tags.AbstractID, "state-abstract");
        state.set(Tags.ConcreteID, "state-concrete");
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.AbstractID, "widget-abstract");
        widget.set(Tags.ConcreteID, "widget-concrete");
        Action first = new Type("first");
        Action second = new Type("second");
        first.mapOriginWidget(widget);
        second.mapOriginWidget(widget);
        Action environment = new ActivateSystem();
        Set<Action> actions = Set.of(first, second, environment);
        DefaultActionIdentifierService service = new DefaultActionIdentifierService();

        assertSame(actions, service.identifyActions(state, actions));
        assertEquals(first.get(Tags.AbstractID), second.get(Tags.AbstractID));
        assertNotEquals(first.get(Tags.ConcreteID), second.get(Tags.ConcreteID));
        String abstractId = environment.get(Tags.AbstractID);
        String concreteId = environment.get(Tags.ConcreteID);
        assertSame(environment, service.identifyEnvironmentAction(state, environment));
        assertEquals(abstractId, environment.get(Tags.AbstractID));
        assertEquals(concreteId, environment.get(Tags.ConcreteID));
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
