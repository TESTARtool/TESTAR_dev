package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.TestReport;
import nl.ou.testar.report.ReportDataAccess;
import nl.ou.testar.report.ReportDataException;

import org.fruit.monkey.ProtocolDelegate;
import org.testar.monkey.alayer.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseTestReport implements TestReport {

    private ReportDataAccess sqlService;

    private State latestState = null;
    private int reportId = 0;
    private int iterationId = 0;

    private Map<Action, Integer> actionIds;
    private Map<State, Integer> stateIds;
    private Map<Action, State> actionTargets;

    private Set<Action> pendingActions;
    private Map<Action, State> pendingSelectedActions;

    private boolean isFirstFailure;

    protected ProtocolDelegate delegate;

    public ProtocolDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ProtocolDelegate delegate) {
        this.delegate = delegate;
    }

    public DatabaseTestReport(ReportDataAccess sqlService, String reportTag) {
        this.sqlService = sqlService;

        actionIds = new HashMap<>();
        stateIds = new HashMap<>();
        actionTargets = new HashMap<>();

        pendingActions = new HashSet<>();
        pendingSelectedActions = new HashMap<>();

        isFirstFailure = true;

        try {
            reportId = sqlService.registerReport(reportTag);
        }
        catch (ReportDataException e) {
            System.err.println("Could not add a report");
            e.printStackTrace();
            if (delegate != null) {
                delegate.popupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void addState(State state) {
        latestState = state;
        try {
            stateIds.put(state, sqlService.findState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID)));
        }
        catch (ReportDataException e) {
            System.err.println("Could not add a state: " + e.getMessage());
            e.printStackTrace();
            if (delegate != null) {
                delegate.popupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void addActions(Set<Action> actions) {
        pendingActions.addAll(actions);
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        pendingSelectedActions.put(action, state);
    }

    @Override
    public void addTestVerdict(Verdict verdict, Action action, State state) {

        try {
            iterationId = sqlService.registerIteration(reportId, verdict.info(), verdict.severity());

            for (Action pendingAction: pendingActions) {
                int actionId = addAction(state, pendingAction, false);
                actionIds.put(pendingAction, actionId);
            }
            for (Map.Entry<Action, State> pendingEntry: pendingSelectedActions.entrySet()) {
                Action pendingAction = pendingEntry.getKey();
                int actionId = addAction(pendingEntry.getValue(), pendingAction, true);
                actionIds.put(pendingAction, actionId);
            }

            int actionId = 0;
            if (action != null) {
                Integer storedActionId = actionIds.get(action);
                if (storedActionId != null) {
                    System.out.println(String.format("=== Lost action: %s ===", action.toString()));
                    actionId = storedActionId;
                }
            }
            int stateId = 0;
            if (state != null) {
                Integer storedStateId = stateIds.get(state);
                if (storedStateId != null) {
                    System.out.println(String.format("=== Lost state: %s ===", state.toString()));
                    stateId = storedStateId;
                }
            }

            sqlService.setSelectionInIteration(iterationId, actionId, stateId);

//            for (Action pendingAction: pendingActions) {
//                System.out.println("Adding action to iteration " + iterationId);
//                sqlService.addActionToIteration(actionIds.get(pendingAction), iterationId);
//            }
//            for (Map.Entry<Action, State> pendingEntry: pendingSelectedActions.entrySet()) {
//                System.out.println("Adding selected action to iteration " + stateIds.get(pendingEntry.getValue()));
//                sqlService.addActionToIteration(actionIds.get(pendingEntry.getKey()), stateIds.get(pendingEntry.getValue()));
//            }
        }
        catch (ReportDataException e) {
            System.err.println("Could not add a test verdict");
            e.printStackTrace();
            if (delegate != null) {
                delegate.popupMessage(e.getMessage());
            }
        }
        finally {
            pendingActions.clear();
            pendingSelectedActions.clear();
        }
    }

    @Override
    public void saveReport(int actionsPerSequence, int totalSequences, String url) {
        try {
            sqlService.finalizeReport(reportId, actionsPerSequence, totalSequences, url);
        }
        catch (ReportDataException e) {
            System.err.println("Cannot finalize report: " + e.getMessage());
            e.printStackTrace();
            if (delegate != null) {
                delegate.popupMessage(e.getMessage());
            }
        }
    }

    private int addAction(State state, Action action, boolean selected) throws ReportDataException {

        int stateId = sqlService.findState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID));

        Timestamp timestamp = null;
        if( state.get(Tags.TimeStamp, null) != null) {
            timestamp = new Timestamp(state.get(Tags.TimeStamp));
        }

        // TODO: optimize actions saving
        int targetStateId = 0;
        State targetState = actionTargets.get(action);
        if (targetState != null) {
            targetStateId = stateIds.get(targetState);
        }
        String widgetPath = "";
        final Widget widget = action.get(Tags.OriginWidget, null);
        if (widget != null) {
            widgetPath = widget.get(Tags.Path, "");
        }
        int actionId = sqlService.registerAction(action.toShortString(), action.toString(),
                    state.get(Tags.OracleVerdict).verdictSeverityTitle(), state.get(Tags.ScreenshotPath, null),
                    timestamp, selected, stateId, targetStateId, widgetPath);
        sqlService.addActionToIteration(actionId, iterationId);
        return actionId;
    }

    public void setTargetState(Action action, State state) {

        actionTargets.put(action, state);
        System.out.println("Target added");

//        int actionId = actionIds.get(action);
//        int stateId = stateIds.get(state);
//        if (actionId >= 0 && stateId >= 0) try {
//            sqlService.registerTargetState(actionId, stateId);
//        }
//        catch (SQLException e) {
//            System.err.println("Cannot set target state: " + e.getMessage());
//            e.printStackTrace();
//        }
    }
}
