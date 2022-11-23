package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.TestReport;
import nl.ou.testar.report.ReportDataAccess;
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
        catch (SQLException e) {
            System.err.println("Could not add a report");
            e.printStackTrace();
        }
    }

    @Override
    public void addState(State state) {
        latestState = state;
        try {
            stateIds.put(state, sqlService.findState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID)));
        }
        catch (SQLException e) {
            System.err.println("Could not add a state: " + e.getMessage());
            e.printStackTrace();
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
                System.out.println("*** Storing regular action ***");
                int actionId = addAction(state, pendingAction, false);
                actionIds.put(pendingAction, actionId);
            }
            for (Map.Entry<Action, State> pendingEntry: pendingSelectedActions.entrySet()) {
                System.out.println("*** Storing selected action ***");
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
        catch (SQLException e) {
            System.err.println("Could not add a test verdict");
            e.printStackTrace();
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
        catch (SQLException e) {
            System.err.println("Cannot finalize report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int addAction(State state, Action action, boolean selected) throws SQLException {

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
        System.out.println(String.format("*** Action: %s ***", action.getClass().getSimpleName()));
        final Widget widget = action.get(Tags.OriginWidget, null);
        if (widget != null) {
            widgetPath = widget.get(Tags.Path, "");
        }
        int actionId = sqlService.registerAction(action.toShortString(), action.toString(),
                    state.get(Tags.OracleVerdict).verdictSeverityTitle(), state.get(Tags.ScreenshotPath, null),
                    timestamp, selected, stateId, targetStateId, widgetPath);
        System.out.println("Action added");
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
