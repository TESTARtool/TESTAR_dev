/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.NOP;
import org.testar.core.action.Observation;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.core.state.SUTBase;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.statemodel.DummyModelManager;
import org.testar.stub.StateStub;

public class StateModelSessionFlowTest {

    @Test
    public void testObservationLinksStates() {
        State firstState = createState("SC1", "SA1");
        State secondState = createState("SC2", "SA2");
        RecordingStateModelManager stateModelManager = new RecordingStateModelManager();
        RecordingActionDerivationService actionDerivationService = new RecordingActionDerivationService();
        StateModelSessionFlow flow = createFlow(stateModelManager, actionDerivationService, firstState, secondState);

        flow.observeState(firstState, deriveActions(actionDerivationService, firstState));
        flow.observeState(secondState, deriveActions(actionDerivationService, secondState));

        Assert.assertEquals(3, stateModelManager.events.size());
        Assert.assertEquals("state:SC1", stateModelManager.events.get(0));
        Assert.assertEquals("action:Observe the SUT", stateModelManager.events.get(1));
        Assert.assertEquals("state:SC2", stateModelManager.events.get(2));
        Assert.assertEquals(2, actionDerivationService.fetchedStates.size());

        Action observationAction = stateModelManager.executedActions.get(0);
        Assert.assertTrue(observationAction instanceof Observation);
        Assert.assertEquals("AA_OBSERVATION", observationAction.get(Tags.AbstractID));
        Assert.assertEquals("AC_OBSERVATION_SC1_SC2", observationAction.get(Tags.ConcreteID));
        Assert.assertSame(firstState, observationAction.get(Tags.OriginWidget));
    }

    @Test
    public void testRealActionLinksStates() {
        State firstState = createState("SC1", "SA1");
        State secondState = createState("SC2", "SA2");
        RecordingStateModelManager stateModelManager = new RecordingStateModelManager();
        RecordingActionDerivationService actionDerivationService = new RecordingActionDerivationService();
        StateModelSessionFlow flow = createFlow(stateModelManager, actionDerivationService, firstState, secondState);
        Action realAction = createRealAction();

        flow.observeState(firstState, deriveActions(actionDerivationService, firstState));
        flow.syncObservationBeforeExecution(firstState);
        flow.markPendingExecutedAction(realAction);
        flow.observeState(secondState, deriveActions(actionDerivationService, secondState));

        Assert.assertEquals(3, stateModelManager.events.size());
        Assert.assertEquals("state:SC1", stateModelManager.events.get(0));
        Assert.assertEquals("action:Real action", stateModelManager.events.get(1));
        Assert.assertEquals("state:SC2", stateModelManager.events.get(2));
        Assert.assertSame(realAction, stateModelManager.executedActions.get(0));
    }

    @Test
    public void testDoNotCreateObservationTransitionForUnchangedState() {
        State sameState = createState("SC1", "SA1");
        RecordingStateModelManager stateModelManager = new RecordingStateModelManager();
        RecordingActionDerivationService actionDerivationService = new RecordingActionDerivationService();
        StateModelSessionFlow flow = createFlow(stateModelManager, actionDerivationService, sameState, sameState);

        flow.observeState(sameState, deriveActions(actionDerivationService, sameState));
        flow.observeState(sameState, Collections.emptySet());

        Assert.assertEquals(1, stateModelManager.events.size());
        Assert.assertEquals("state:SC1", stateModelManager.events.get(0));
        Assert.assertTrue(stateModelManager.executedActions.isEmpty());
        Assert.assertEquals(1, actionDerivationService.fetchedStates.size());
    }

    @Test
    public void testFlushPendingExecutedActionDuringFinalObservation() {
        State firstState = createState("SC1", "SA1");
        State secondState = createState("SC2", "SA2");
        RecordingStateModelManager stateModelManager = new RecordingStateModelManager();
        RecordingActionDerivationService actionDerivationService = new RecordingActionDerivationService();
        StateModelSessionFlow flow = createFlow(stateModelManager, actionDerivationService, firstState, secondState);
        Action realAction = createRealAction();

        flow.observeState(firstState, deriveActions(actionDerivationService, firstState));
        flow.syncObservationBeforeExecution(firstState);
        flow.markPendingExecutedAction(realAction);
        flow.finalizePendingObservation(secondState, deriveActions(actionDerivationService, secondState));

        Assert.assertEquals(3, stateModelManager.events.size());
        Assert.assertEquals("state:SC1", stateModelManager.events.get(0));
        Assert.assertEquals("action:Real action", stateModelManager.events.get(1));
        Assert.assertEquals("state:SC2", stateModelManager.events.get(2));
        Assert.assertSame(realAction, stateModelManager.executedActions.get(0));
    }

    private StateModelSessionFlow createFlow(RecordingStateModelManager stateModelManager,
                                             RecordingActionDerivationService actionDerivationService,
                                             State... states) {
        PlatformServices services = new PlatformServices(
                new NoOpSystemService(),
                new QueueStateService(states),
                new NoOpOracleEvaluationService(),
                stateModelManager,
                actionDerivationService,
                new NoOpActionSelectorService(),
                new NoOpActionResolver(),
                new NoOpActionExecutionService()
        );
        return new StateModelSessionFlow(services, new FakeSut());
    }

    private Set<Action> deriveActions(RecordingActionDerivationService actionDerivationService, State state) {
        return actionDerivationService.deriveActions(new FakeSut(), state);
    }

    private State createState(String concreteId, String abstractId) {
        StateStub state = new StateStub();
        state.set(Tags.ConcreteID, concreteId);
        state.set(Tags.AbstractID, abstractId);
        return state;
    }

    private Action createRealAction() {
        NOP action = new NOP();
        action.set(Tags.Desc, "Real action");
        action.set(Tags.AbstractID, "AA_REAL");
        action.set(Tags.ConcreteID, "AC_REAL");
        return action;
    }

    private static final class RecordingStateModelManager extends DummyModelManager {

        private final List<String> events = new ArrayList<>();
        private final List<Action> executedActions = new ArrayList<>();

        @Override
        public void notifyNewStateReached(State newState, Set<Action> actions) {
            events.add("state:" + newState.get(Tags.ConcreteID));
        }

        @Override
        public void notifyActionExecution(Action action) {
            executedActions.add(action);
            events.add("action:" + action.get(Tags.Desc));
        }
    }

    private static final class RecordingActionDerivationService implements ActionDerivationService {

        private final List<State> fetchedStates = new ArrayList<>();

        @Override
        public Set<Action> deriveActions(SUT system, State state) {
            fetchedStates.add(state);
            return Collections.emptySet();
        }
    }

    private static final class QueueStateService implements StateService {

        private final Deque<State> states;

        private QueueStateService(State... states) {
            this.states = new ArrayDeque<>(List.of(states));
        }

        @Override
        public State getState(SUT system) {
            if (states.isEmpty()) {
                throw new IllegalStateException("No state prepared for test");
            }
            return states.removeFirst();
        }
    }

    private static final class NoOpSystemService implements SystemService {

        @Override
        public SUT startSystem() {
            return new FakeSut();
        }

        @Override
        public void stopSystem(SUT system) {
        }
    }

    private static final class NoOpOracleEvaluationService implements OracleEvaluationService {

        @Override
        public List<Verdict> getVerdicts(SUT system, State state) {
            return Collections.emptyList();
        }

        @Override
        public void addVerdict(State state, Verdict verdict) {
        }
    }

    private static final class NoOpActionSelectorService implements ActionSelectorService {

        @Override
        public Action selectAction(State state, Set<Action> actions) {
            return null;
        }
    }

    private static final class NoOpActionResolver implements ActionResolver {

        @Override
        public ResolvedAction resolve(Iterable<Action> actions, List<String> arguments) {
            return null;
        }
    }

    private static final class NoOpActionExecutionService implements ActionExecutionService {

        @Override
        public boolean executeAction(SUT system, State state, Action action) {
            return true;
        }
    }

    private static final class FakeSut extends SUTBase {

        @Override
        public void stop() {
        }

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public String getStatus() {
            return "";
        }

        @Override
        public void setNativeAutomationCache() {
        }
    }
}
