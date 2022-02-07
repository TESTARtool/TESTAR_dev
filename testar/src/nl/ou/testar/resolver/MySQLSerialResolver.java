package nl.ou.testar.resolver;

import nl.ou.testar.ActionResolver;
import nl.ou.testar.parser.ActionParser;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.mysql.MySqlService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MySQLSerialResolver implements ActionResolver {

    private ActionResolver nextResolver;
    private MySqlService service;
    private int reportId = -1;

    private int currentIterationId = -1;
    private int currentActionId = -1;
    private Timestamp currentActionTime = null;

    private final ActionParser parser = new ActionParser();

    public void startReplay(MySqlService service, String reportTag) throws SQLException {
        this.service = service;
        this.reportId = service.getReportId(reportTag);
    }

    public int startFirstIteration() throws SQLException {
        if (reportId < 0) {
            return -1;
        }
        currentIterationId = service.getFirstIterationId(reportId);
        return currentIterationId;
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        if (currentIterationId >= 0) try {
            MySqlService.ActionData actionData =
                    (currentActionId < 0 ? service.getFirstAction(currentIterationId) : service.getNextAction(currentIterationId, currentActionTime));
            if (actionData != null) {
                currentActionId = actionData.getId();
                currentActionTime = actionData.getStartTime();
                final Action action = parser.parse(actionData.getDescription()).left();
                if (action != null) {
                    return new HashSet<>(Collections.singletonList(action));
                }
            }
            else {
                currentActionId = -1;
                currentActionTime = null;
            }
        }
        catch (Exception e) {
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
    public ActionResolver nextResolver() {
        return nextResolver;
    }

    @Override
    public void setNextResolver(ActionResolver nextResolver) {
        this.nextResolver = nextResolver;
    }
}
