package org.testar.engine.service;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.system.SystemCompositionPlan;

public final class ComposedSystemServiceTest {

    @Test
    public void startSystemDelegatesAndRunsHooks() throws SystemStartException {
        TestSut expectedSystem = new TestSut();
        AtomicReference<SUT> receivedSystem = new AtomicReference<>();
        ComposedSystemService service = ComposedSystemService.compose(
                new SystemCompositionPlan(
                        new TestSystemService(expectedSystem),
                        Collections.singletonList(receivedSystem::set),
                        Collections.emptyList()
                )
        );

        SUT system = service.startSystem();

        Assert.assertSame(expectedSystem, system);
        Assert.assertSame(expectedSystem, receivedSystem.get());
    }

    @Test
    public void stopSystemRunsHooksBeforeDelegating() {
        AtomicBoolean hookCalled = new AtomicBoolean(false);
        AtomicBoolean delegateCalled = new AtomicBoolean(false);
        TestSut system = new TestSut();
        SystemService delegate = new SystemService() {
            @Override
            public SUT startSystem() {
                return system;
            }

            @Override
            public void stopSystem(SUT currentSystem) {
                Assert.assertTrue(hookCalled.get());
                delegateCalled.set(true);
            }
        };
        ComposedSystemService service = ComposedSystemService.compose(
                new SystemCompositionPlan(
                        delegate,
                        Collections.emptyList(),
                        Collections.singletonList(currentSystem -> hookCalled.set(true))
                )
        );

        service.stopSystem(system);

        Assert.assertTrue(delegateCalled.get());
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

    private static final class TestSystemService implements SystemService {

        private final SUT system;

        private TestSystemService(SUT system) {
            this.system = system;
        }

        @Override
        public SUT startSystem() {
            return system;
        }

        @Override
        public void stopSystem(SUT system) {
        }
    }
}
