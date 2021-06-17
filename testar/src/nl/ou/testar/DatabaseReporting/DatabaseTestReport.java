package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.TestReport;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.Settings;
import org.fruit.monkey.mysql.MySqlService;
import org.fruit.monkey.mysql.MySqlServiceImpl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseTestReport implements TestReport {

    private MySqlService sqlService;

    private State latestState = null;
    private int reportId = -1;
    private int iterationId = -1;

    private Map<Action, Integer> actionIds;
    private Map<State, Integer> stateIds;

    private Set<Action> pendingActions;
    private Map<Action, State> pendingSelectedActions;

    private boolean isFirstFailure;

    public DatabaseTestReport(MySqlService sqlService, String reportTag) {
        this.sqlService = sqlService;

        actionIds = new HashMap<>();
        stateIds = new HashMap<>();

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
                int actionId = addAction(state, pendingAction);
                actionIds.put(pendingAction, actionId);
            }
            for (Map.Entry<Action, State> pendingEntry: pendingSelectedActions.entrySet()) {
                Action pendingAction = pendingEntry.getKey();
                int actionId = addAction(pendingEntry.getValue(), pendingAction);
                actionIds.put(pendingAction, actionId);
            }

            int actionId = -1;
            if (action != null) {
                actionId = actionIds.get(action);
            }
            int stateId = -1;
            if (state != null) {
                stateId = stateIds.get(state);
            }

            sqlService.setSelectionInIteration(iterationId, actionId, stateId);

            for (Action pendingAction: pendingActions) {
                sqlService.addActionToIteration(actionIds.get(pendingAction), iterationId);
            }
            for (Map.Entry<Action, State> pendingEntry: pendingSelectedActions.entrySet()) {
                sqlService.addActionToIteration(actionIds.get(pendingEntry.getKey()), stateIds.get(pendingEntry.getValue()));
            }
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

    private int addAction(State state, Action action) throws SQLException {
        return sqlService.registerAction(action.toShortString(), action.toString(),
                    state.get(Tags.OracleVerdict).verdictSeverityTitle(), state.get(Tags.ScreenshotPath),
                    new Timestamp(state.get(Tags.TimeStamp)));
    }
}
