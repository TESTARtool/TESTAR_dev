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
import java.util.Set;

public class DatabaseTestReport implements TestReport {

    private MySqlService sqlService;

    private State latestState = null;
    private int reportId = -1;
    private int iterationId = -1;

    private HashMap<Action, Integer> actionIds;
    private HashMap<State, Integer> stateIds;

    public DatabaseTestReport(MySqlService sqlService) {
        this.sqlService = sqlService;
        actionIds = new HashMap<>();
        stateIds = new HashMap<>();
    }

    @Override
    public void addState(State state) {
        latestState = state;
        try {
            stateIds.put(state, sqlService.findState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID)));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addActions(Set<Action> actions) {
        for (Action action: actions) {
            try {
                int actionId = sqlService.registerAction(iterationId, action.toShortString(), action.toString(),
                        latestState.get(Tags.OracleVerdict).verdictSeverityTitle(), latestState.get(Tags.ScreenshotPath),
                        new Timestamp(latestState.get(Tags.TimeStamp)));
                actionIds.put(action, actionId);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        try {
            sqlService.registerAction(iterationId, action.toShortString(), action.toString(),
                    state.get(Tags.OracleVerdict).verdictSeverityTitle(), state.get(Tags.ScreenshotPath),
                    new Timestamp(state.get(Tags.TimeStamp)));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTestVerdict(Verdict verdict, Action action, State state) {
        int actionId = actionIds.get(action);
        int stateId = stateIds.get(state);
        try {
            iterationId = sqlService.registerIteration(reportId, verdict.info(), verdict.severity(), actionId, stateId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveReport(int actionsPerSequence, int totalSequences, String url) {

    }
}
