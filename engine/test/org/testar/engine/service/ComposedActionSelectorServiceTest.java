package org.testar.engine.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.stub.StateStub;

public final class ComposedActionSelectorServiceTest {

    @Test
    public void returnsPrimarySelectionWhenAvailable() {
        TestAction expectedAction = new TestAction("primary");
        ComposedActionSelectorService service = ComposedActionSelectorService.compose(
                ActionSelectorPlan.withFallback(
                        (state, actions) -> expectedAction,
                        (state, actions) -> new TestAction("fallback")
                )
        );

        Action selectedAction = service.selectAction(new StateStub(), Collections.emptySet());

        assertSame(expectedAction, selectedAction);
    }

    @Test
    public void fallsBackWhenPrimarySelectionIsMissing() {
        TestAction fallbackAction = new TestAction("fallback");
        AtomicBoolean fallbackCalled = new AtomicBoolean(false);
        ActionSelectorService fallbackSelector = (state, actions) -> {
            fallbackCalled.set(true);
            return fallbackAction;
        };
        ComposedActionSelectorService service = ComposedActionSelectorService.compose(
                ActionSelectorPlan.withFallback(
                        (state, actions) -> null,
                        fallbackSelector
                )
        );

        Action selectedAction = service.selectAction(new StateStub(), Collections.emptySet());

        assertSame(fallbackAction, selectedAction);
        assertEquals(true, fallbackCalled.get());
    }

    private static final class TestAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private final String description;

        private TestAction(String description) {
            this.description = description;
            set(org.testar.core.tag.Tags.Role, ActionRoles.NOPAction);
        }

        @Override
        public void run(org.testar.core.state.SUT system,
                        org.testar.core.state.State state,
                        double duration) {
        }

        @Override
        public String toShortString() {
            return description;
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(org.testar.core.alayer.Role... discardParameters) {
            return description;
        }
    }
}
