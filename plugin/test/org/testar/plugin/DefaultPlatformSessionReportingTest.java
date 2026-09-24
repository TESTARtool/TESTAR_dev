package org.testar.plugin;

import org.junit.Test;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.reporting.Reporting;
import org.testar.statemodel.StateModelManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public final class DefaultPlatformSessionReportingTest {

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
