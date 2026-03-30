package org.testar.windows.service;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;

public final class WindowsSystemServiceTest {

    @Test
    public void startSystemDelegatesToStarter() throws SystemStartException {
        final SUT expectedSystem = new TestSut();
        WindowsSystemService service = new WindowsSystemService(() -> expectedSystem);

        Assert.assertSame(expectedSystem, service.startSystem());
    }

    @Test
    public void stopSystemStopsProvidedSystem() {
        TestSut system = new TestSut();
        WindowsSystemService service = new WindowsSystemService(TestSut::new);

        service.stopSystem(system);

        Assert.assertTrue(system.stopped);
    }

    private static final class TestSut extends TaggableBase implements SUT {

        private static final long serialVersionUID = 1L;

        private boolean stopped;

        @Override
        public void stop() throws SystemStopException {
            stopped = true;
        }

        @Override
        public boolean isRunning() {
            return !stopped;
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
