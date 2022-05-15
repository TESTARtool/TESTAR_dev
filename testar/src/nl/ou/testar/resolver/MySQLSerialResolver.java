package nl.ou.testar.resolver;

import nl.ou.testar.ActionResolver;
import nl.ou.testar.parser.ActionParseException;
import nl.ou.testar.parser.ActionParser;
import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.monkey.Settings;
import org.fruit.monkey.mysql.MySqlService;

import java.sql.SQLException;
import java.util.*;

public class MySQLSerialResolver extends SerialResolver {

    private ActionResolver nextResolver;
    private MySqlService service;
    private int reportId = -1;

    private MySqlService.ActionData currentActionData = null;

    private final ActionParser parser = new ActionParser();

    private List<MySqlService.IterationData> iterations = null;
    private List<MySqlService.ActionData> actions = null;
    private Iterator<MySqlService.IterationData> outerIterator = null;
    private Iterator<MySqlService.ActionData> innerIterator = null;

    public MySQLSerialResolver(MySqlService service, Settings settings) {
        super(service, settings);
    }

    public void startReplay(String reportTag) throws SQLException {
        this.reportId = service.getReportId(reportTag);

        iterations = service.getAllIterations(reportId);
        outerIterator = iterations.listIterator();
    }

    @Override
    protected Action nextAction(SUT system, State state) {
        final MySqlService.ActionData currentActionData = innerIterator.next();
        if (currentActionData == null) {
            return null;
        }
        try {
            Pair<Action, String> parseResult = parser.parse(currentActionData.getDescription().replace("\n", " "));
            final Action action = parseResult.left();
            final String widgetPath = currentActionData.getWidgetPath();
            if (widgetPath != null && widgetPath.length() > 0) {
                for (Widget widget: state) {
                    if (widgetPath.equals(widget.get(Tags.Path))) {
                        action.set(Tags.OriginWidget, widget);
                    }
                }
            }
            return action;
        }
        catch (ActionParseException e) {
            throw new ActionBuildException(e.getMessage());
        }
    }

    @Override
    public boolean moreActions(State state) {
        if (innerIterator == null || !innerIterator.hasNext()) {
            return false;
        }
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
}
