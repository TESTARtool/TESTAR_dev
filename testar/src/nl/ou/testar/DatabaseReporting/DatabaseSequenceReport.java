package nl.ou.testar.DatabaseReporting;

import nl.ou.testar.SequenceReport;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.mysql.MySqlService;

import java.util.Set;

public class DatabaseSequenceReport implements SequenceReport {

    private MySqlService sqlService;

    public DatabaseSequenceReport(MySqlService sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void addState(State state) {

    }

    @Override
    public void addActions(Set<Action> actions) {

    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {

    }

    @Override
    public void addSelectedAction(State state, Action action) {

    }

    @Override
    public void addTestVerdict(Verdict verdict) {

    }

    @Override
    public void close() {

    }
}
