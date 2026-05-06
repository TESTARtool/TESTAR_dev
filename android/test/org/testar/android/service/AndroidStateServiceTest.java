package org.testar.android.service;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.state.SUTBase;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.stub.StateStub;

public final class AndroidStateServiceTest {

    @Test
    public void delegatesToUnderlyingStateBuilder() {
        StateStub expected = new StateStub();
        StateBuilder stateBuilder = system -> expected;
        AndroidStateService service = new AndroidStateService(stateBuilder);

        State actual = service.getState(new TestSut());

        Assert.assertSame(expected, actual);
    }

    private static final class TestSut extends SUTBase {

        @Override
        public void stop() throws SystemStopException {
        }

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public String getStatus() {
            return "running";
        }

        @Override
        public void setNativeAutomationCache() {
        }
    }
}
