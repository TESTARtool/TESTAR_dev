package org.testar.plugin;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.InOrder;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.reporting.Reporting;
import org.testar.statemodel.StateModelManager;
import org.testar.stub.StateStub;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public final class DefaultPlatformSessionReportingTest {

    @Test
    public void finalVerdictsReachTheModelAfterTheFinalObservation() {
        PlatformServices services = services();
        StateStub firstState = new StateStub();
        firstState.set(Tags.ConcreteID, "SC1");
        StateStub finalState = new StateStub();
        finalState.set(Tags.ConcreteID, "SC2");
        when(services.stateService().getState(any())).thenReturn(firstState, finalState);
        when(services.actionDerivationService().deriveActions(any(), any())).thenReturn(Set.of());
        DefaultPlatformSession session = new DefaultPlatformSession(services, mock(SUT.class),
                SessionReportingManager.deferred("https://example.org"), false);
        List<Verdict> verdicts = List.of(new Verdict(Verdict.Severity.LLM_INVALID, "Goal outcome is incorrect."));
        session.getDerivedActions();

        session.stopSystem(verdicts);

        InOrder order = inOrder(services.stateModelManager(), services.systemService());
        order.verify(services.stateModelManager()).notifyTestSequencedStarted();
        order.verify(services.stateModelManager()).notifyNewStateReached(firstState, Set.of());
        order.verify(services.stateModelManager()).notifyActionExecution(any());
        order.verify(services.stateModelManager()).notifyNewStateReached(finalState, Set.of());
        order.verify(services.stateModelManager()).notifyTestSequenceStopped(verdicts);
        order.verify(services.systemService()).stopSystem(session.system());
        assertNull(finalState.get(Tags.OracleVerdicts, null));
    }

    @Test
    public void spyStyleSessionDoesNotFinishReporting() {
        Reporting reporting = mock(Reporting.class);
        SessionReportingManager manager = SessionReportingManager.deferred("https://example.org");
        manager.bindReporting(reporting);
        DefaultPlatformSession session = new DefaultPlatformSession(services(), mock(SUT.class), manager, false);

        session.close();

        verifyNoInteractions(reporting);
    }

    @Test
    public void reportingEnabledSessionFinishesReportingOnClose() {
        Reporting reporting = mock(Reporting.class);
        SessionReportingManager manager = SessionReportingManager.deferred("https://example.org");
        manager.bindReporting(reporting);
        DefaultPlatformSession session = new DefaultPlatformSession(services(), mock(SUT.class), manager, true);

        session.close();

        verify(reporting).finishReport();
    }

    private PlatformServices services() {
        return new PlatformServices(
                mock(SystemService.class),
                mock(StateService.class),
                mock(OracleEvaluationService.class),
                mock(StateModelManager.class),
                mock(ActionDerivationService.class),
                mock(ActionSelectorService.class),
                mock(ActionResolver.class),
                mock(ActionExecutionService.class)
        );
    }
}
