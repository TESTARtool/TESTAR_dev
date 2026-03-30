package org.testar.windows.service;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;
import org.testar.stub.StateStub;

public final class WindowsStateServiceTest {

    @Test
    public void getStateDelegatesToConfiguredBuilder() throws StateBuildException {
        final State expectedState = new StateStub();
        final SUT system = new TestSut();
        StateBuilder builder = currentSystem -> {
            Assert.assertSame(system, currentSystem);
            return expectedState;
        };

        WindowsStateService service = new WindowsStateService(builder, () -> { });

        Assert.assertSame(expectedState, service.getState(system));
    }

    @Test
    public void closeDelegatesToConfiguredReleaser() {
        AtomicBoolean released = new AtomicBoolean(false);
        WindowsStateService service = new WindowsStateService(
                currentSystem -> new StateStub(),
                () -> released.set(true)
        );

        service.close();

        Assert.assertTrue(released.get());
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
