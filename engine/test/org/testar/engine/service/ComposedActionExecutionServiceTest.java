package org.testar.engine.service;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.stub.StateStub;

public final class ComposedActionExecutionServiceTest {

    @Test
    public void executeActionDelegatesAndRunsHooks() {
        AtomicBoolean beforeCalled = new AtomicBoolean(false);
        AtomicReference<Boolean> afterExecuted = new AtomicReference<>();
        ActionExecutionService delegate = (system, state, action) -> {
            Assert.assertTrue(beforeCalled.get());
            return true;
        };
        ComposedActionExecutionService service = ComposedActionExecutionService.compose(
                new ActionExecutionPlan(
                        delegate,
                        Collections.singletonList((system, state, action) -> beforeCalled.set(true)),
                        Collections.singletonList((system, state, action, executed) -> afterExecuted.set(executed))
                )
        );

        boolean executed = service.executeAction(null, new StateStub(), new TestAction());

        Assert.assertTrue(executed);
        Assert.assertEquals(Boolean.TRUE, afterExecuted.get());
    }

    @Test
    public void returnsDelegateExecutionResult() {
        ComposedActionExecutionService service = ComposedActionExecutionService.compose(
                ActionExecutionPlan.basic((system, state, action) -> false)
        );

        boolean executed = service.executeAction(null, new StateStub(), new TestAction());

        Assert.assertFalse(executed);
    }

    private static final class TestAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private TestAction() {
            set(org.testar.core.tag.Tags.Role, ActionRoles.NOPAction);
        }

        @Override
        public void run(SUT system, State state, double duration) {
        }

        @Override
        public String toShortString() {
            return "test";
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(org.testar.core.alayer.Role... discardParameters) {
            return "test";
        }
    }
}
