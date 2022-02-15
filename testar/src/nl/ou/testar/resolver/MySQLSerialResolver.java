package nl.ou.testar.resolver;

import nl.ou.testar.ActionResolver;
import nl.ou.testar.parser.ActionParseException;
import nl.ou.testar.parser.ActionParser;
import org.fruit.monkey.mysql.MySqlService;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.exceptions.ActionBuildException;

import java.sql.SQLException;
import java.util.*;

public class MySQLSerialResolver implements ActionResolver {

    private ActionResolver nextResolver;
    private MySqlService service;
    private int reportId = -1;

    private MySqlService.ActionData currentActionData = null;

    private final ActionParser parser = new ActionParser();

    private List<MySqlService.IterationData> iterations = null;
    private List<MySqlService.ActionData> actions = null;
    private Iterator<MySqlService.IterationData> outerIterator = null;
    private Iterator<MySqlService.ActionData> innerIterator = null;

    public void startReplay(MySqlService service, String reportTag) throws SQLException {
        this.service = service;
        this.reportId = service.getReportId(reportTag);

        iterations = service.getAllIterations(reportId);
        outerIterator = iterations.listIterator();
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        if (currentActionData == null) {
            return null;
        }
        try {
            Pair<Action, String> parseResult = parser.parse(currentActionData.getDescription().replace("\n", " "));

            final Action action = parseResult.left();
            if (action != null) {
                return new HashSet<>(Collections.singletonList(action));
            }
        }
        catch (ActionParseException e) {
            throw new ActionBuildException(e.getMessage());
        }
        return null;
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        // A single action supposed here
        if (actions != null) {
            return actions.iterator().next();
        }
        return null;
    }

    @Override
    public boolean moreActions(State state) {
        if (innerIterator == null || !innerIterator.hasNext()) {
            return false;
        }
        currentActionData = innerIterator.next();
        return true;
    }

    @Override
    public boolean moreSequences() {
        if (outerIterator == null || !outerIterator.hasNext()) {
            return false;
        }
        MySqlService.IterationData iterationData = outerIterator.next();
        try {
            actions = service.getSelectedActions(iterationData.getId());
            innerIterator = actions.listIterator();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public ActionResolver nextResolver() {
        return nextResolver;
    }

    @Override
    public void setNextResolver(ActionResolver nextResolver) {
        this.nextResolver = nextResolver;
    }
}
