package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.SequenceReport;
import nl.ou.testar.report.ReportDataAccess;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

import java.sql.SQLException;
import java.util.Set;

public class DatabaseSequenceReport implements SequenceReport {

    private ReportDataAccess sqlService;

    public DatabaseSequenceReport(ReportDataAccess sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void addState(State state) {
        String concreteIdCustom = state.get(Tags.ConcreteIDCustom);
        String abstractId = state.get(Tags.AbstractID);
        try {
			int searchResult = sqlService.findState(concreteIdCustom, abstractId);
            if (searchResult == 0) {
                int stateId = sqlService.registerState(concreteIdCustom, abstractId,
                        state.get(Tags.Abstract_R_ID), state.get(Tags.Abstract_R_T_ID),
                        state.get(Tags.Abstract_R_T_P_ID));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addActionToState(int stateId, int actionId, boolean visited) {
        try {
            sqlService.registerStateAction(stateId, actionId, visited);
        }
        catch (SQLException e) {
            System.err.println("Could not add action to state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addActions(Set<Action> actions) {
        //TODO
    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        //TODO
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        //TODO
    }

    @Override
    public void addTestVerdict(Verdict verdict) {
        //TODO
    }

    @Override
    public void close() {
        //TODO
    }
}
