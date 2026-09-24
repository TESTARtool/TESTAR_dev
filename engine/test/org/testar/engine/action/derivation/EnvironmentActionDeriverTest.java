package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.testar.core.Pair;
import org.testar.core.action.Action;
import org.testar.core.action.ActivateSystem;
import org.testar.core.action.KillProcess;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.stub.StateStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class EnvironmentActionDeriverTest {

    private static final SessionPolicyContext EMPTY_CONTEXT = mock(SessionPolicyContext.class);

    @Test
    public void derivesKillProcessForMatchingNonSutProcess() {
        State state = new StateStub();
        SUT system = mock(SUT.class);
        when(system.get(Tags.PID, null)).thenReturn(99L);
        when(system.getRunningProcesses()).thenReturn(Collections.singletonList(Pair.from(100L, "calc.exe")));

        Set<Action> actions = new KillProcessesActionDeriver("calc\\.exe").derive(system, state, EMPTY_CONTEXT);

        assertEquals(1, actions.size());
        Action action = actions.iterator().next();
        assertTrue(action instanceof KillProcess);
        assertEquals("Kill Process with name 'calc.exe'", action.get(Tags.Desc));
        assertEquals(state, action.get(Tags.OriginWidget));
    }

    @Test
    public void derivesForegroundActionWhenStateIsNotForeground() {
        StateStub state = new StateStub();
        state.set(Tags.Foreground, false);
        SUT system = mock(SUT.class);
        when(system.get(Tags.SystemActivator, null)).thenReturn(() -> {
        });

        Set<Action> actions = new ForegroundActionDeriver().derive(system, state, EMPTY_CONTEXT);

        assertEquals(1, actions.size());
        assertTrue(actions.iterator().next() instanceof ActivateSystem);
        assertEquals(state, actions.iterator().next().get(Tags.OriginWidget));
    }

    @Test
    public void derivesEscapeFallbackAction() {
        State state = new StateStub();

        Set<Action> actions = new EscFallbackActionDeriver().derive(null, state, EMPTY_CONTEXT);

        assertEquals(1, actions.size());
        assertEquals(state, actions.iterator().next().get(Tags.OriginWidget));
    }
}
