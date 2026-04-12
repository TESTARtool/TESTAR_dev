package org.testar.engine.service;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.stub.StateStub;

public final class ComposedStateServiceTest {

    @Test
    public void getStateDelegatesBuildsIdsAndAppliesPlan() throws StateBuildException {
        StateStub capturedState = new StateStub();
        capturedState.set(Tags.ConcreteID, "captured");
        StateStub projectedState = new StateStub();
        SessionPolicyContext context = new SessionPolicyContext((VisiblePolicy) widget -> true);
        AtomicReference<State> receivedState = new AtomicReference<>();
        AtomicReference<SessionPolicyContext> receivedContext = new AtomicReference<>();
        ComposedStateService service = new ComposedStateService(
                context,
                new StateCompositionPlan(
                        system -> capturedState,
                        (state, policyContext) -> {
                            receivedState.set(state);
                            receivedContext.set(policyContext);
                            Assert.assertNotNull(state.get(Tags.ConcreteID, null));
                            return projectedState;
                        }
                )
        );

        State state = service.getState(new TestSut());

        Assert.assertSame(projectedState, state);
        Assert.assertSame(capturedState, receivedState.get());
        Assert.assertSame(context, receivedContext.get());
    }

    @Test
    public void closeDelegatesToUnderlyingStateService() throws Exception {
        AtomicBoolean closed = new AtomicBoolean(false);
        StateService delegate = new CloseableStateService(closed);
        ComposedStateService service = new ComposedStateService(
                new SessionPolicyContext(),
                StateCompositionPlan.fullState(delegate)
        );

        service.close();

        Assert.assertTrue(closed.get());
    }

    private static final class CloseableStateService implements StateService, AutoCloseable {

        private final AtomicBoolean closed;

        private CloseableStateService(AtomicBoolean closed) {
            this.closed = closed;
        }

        @Override
        public State getState(SUT system) {
            return new StateStub();
        }

        @Override
        public void close() {
            closed.set(true);
        }
    }

    private static final class TestSut extends TaggableBase implements SUT {

        private static final long serialVersionUID = 1L;

        @Override
        public void stop() throws SystemStopException {
        }

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public String getStatus() {
            return "test";
        }

        @Override
        public java.util.List<Pair<Long, String>> getRunningProcesses() {
            return Collections.emptyList();
        }

        @Override
        public void setNativeAutomationCache() {
        }

        @Override
        public AutomationCache getNativeAutomationCache() {
            return null;
        }

        @Override
        protected <T> T fetch(Tag<T> tag) {
            return null;
        }
    }
}
