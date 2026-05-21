package org.testar.engine.service;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.alayer.Role;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.stub.StateStub;

public final class ComposedActionExecutionServiceTest {

    @Test
    public void executeActionDelegatesToConfiguredService() {
        ActionExecutionService delegate = (system, state, action) -> true;
        ComposedActionExecutionService service = ComposedActionExecutionService.compose(
                new ActionExecutionPlan(delegate)
        );

        boolean executed = service.executeAction(null, new StateStub(), new TestAction());

        Assert.assertTrue(executed);
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
            set(Tags.Role, ActionRoles.NOPAction);
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
        public String toString(Role... discardParameters) {
            return "test";
        }
    }
}
