package org.testar.scriptless.util;

import org.junit.Before;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.TestingServices;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerActionUtilTest {

    private StateStub state;
    private SUT system;
    private RuntimeContext runtimeContext;
    private ActionExecutionService actionExecutionService;

    @Before
    public void setUp() {
        state = new StateStub();
        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.setRoot(state);
        widget.set(Tags.Title, "Target");
        widget.set(Tags.ConcreteID, "target-id");
        widget.set(Tags.Shape, Rect.from(0, 0, 100, 30));
        state.addChild(widget);

        system = mock(SUT.class);
        runtimeContext = mock(RuntimeContext.class);
        TestingServices testingServices = mock(TestingServices.class);
        actionExecutionService = mock(ActionExecutionService.class);
        when(runtimeContext.testingServices()).thenReturn(testingServices);
        when(testingServices.actionExecutionService()).thenReturn(actionExecutionService);
    }

    @Test
    public void matchingClickReportsExecutionResult() {
        when(actionExecutionService.executeAction(eq(system), eq(state), any(Action.class)))
                .thenReturn(false, true);

        assertFalse(TriggerActionUtil.clickMatchingWidget(
                Tags.Title, "Target", state, system, runtimeContext, 1, 0));
        assertTrue(TriggerActionUtil.clickMatchingWidget(
                Tags.Title, "Target", state, system, runtimeContext, 1, 0));
    }

    @Test
    public void matchingTypeAndPasteReportExecutionFailure() {
        when(actionExecutionService.executeAction(eq(system), eq(state), any(Action.class)))
                .thenReturn(false);

        assertFalse(TriggerActionUtil.typeMatchingWidget(
                Tags.Title, "Target", "value", state, system, runtimeContext, 1, 0));
        assertFalse(TriggerActionUtil.pasteMatchingWidget(
                Tags.Title, "Target", "value", state, system, runtimeContext, 1, 0));
    }
}
