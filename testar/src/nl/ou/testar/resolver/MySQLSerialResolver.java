package nl.ou.testar.resolver;

import nl.ou.testar.ActionResolver;
import nl.ou.testar.parser.ActionParseException;
import nl.ou.testar.parser.ActionParser;
import nl.ou.testar.report.ReportDataAccess;
import org.testar.monkey.Pair;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;

import java.sql.SQLException;
import java.util.*;

public class MySQLSerialResolver extends SerialResolver<ReportDataAccess> {

    private ActionResolver nextResolver;
    private int reportId = 0;

    private ReportDataAccess.ActionData currentActionData = null;

    private final ActionParser parser = new ActionParser();

    private List<ReportDataAccess.IterationData> iterations = null;
    private List<ReportDataAccess.ActionData> actions = null;
    private Iterator<ReportDataAccess.IterationData> outerIterator = null;
    private Iterator<ReportDataAccess.ActionData> innerIterator = null;

    public MySQLSerialResolver(ReportDataAccess service, Settings settings) {
        super(service, settings);
    }

    public void startReplay(String reportTag) throws Exception {
        super.startReplay(reportTag);
        System.out.println(String.format("*** Service: %s, tag: %s ***", service, reportTag));
        this.reportId = service.getReportId(reportTag);

        iterations = service.getAllIterations(reportId);
        outerIterator = iterations.listIterator();
    }

    @Override
    protected Action nextAction(SUT system, State state) {
        if(!innerIterator.hasNext()) {
            return null;
        }
        final ReportDataAccess.ActionData currentActionData = innerIterator.next();
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
    public Action selectAction(SUT system, State state, Set<Action> actions) {
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
        return true;
    }

    @Override
    public boolean moreSequences() {
        if (outerIterator == null || !outerIterator.hasNext()) {
            return false;
        }
        ReportDataAccess.IterationData iterationData = outerIterator.next();
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
