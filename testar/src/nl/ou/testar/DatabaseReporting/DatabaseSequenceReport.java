package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.SequenceReport;
import org.fruit.alayer.*;
import org.fruit.monkey.mysql.MySqlService;

import java.sql.SQLException;
import java.util.Set;

public class DatabaseSequenceReport implements SequenceReport {

    private MySqlService sqlService;

    public DatabaseSequenceReport(MySqlService sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void addState(State state) {
        try {
            sqlService.registerState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID),
                    state.get(Tags.Abstract_R_ID), state.get(Tags.Abstract_R_T_ID),
                    state.get(Tags.Abstract_R_T_P_ID));
        }
        catch (SQLException e) {
            System.err.println("Could not add a state: " + e.getMessage());
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
