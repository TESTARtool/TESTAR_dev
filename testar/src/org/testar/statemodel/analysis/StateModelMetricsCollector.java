package org.testar.statemodel.analysis;

import com.google.gson.Gson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.statemodel.StateModelManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateModelMetricsCollector implements IMetricsCollector {
    protected static final Logger logger = LogManager.getLogger();
    private Gson gson;

    private String searchMessage = "";

    private List<StateModelMetrics> metrics = new ArrayList<>();

    public StateModelMetricsCollector() {
    }

    public StateModelMetricsCollector(String searchMessage) {
        this.searchMessage = searchMessage;
        gson = new Gson();
    }

    // SELECT COUNT(*) AS uniqueStates FROM ConcreteState WHERE uid like X
    private int getUniqueStates(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS uniqueStates ");
        queryBuilder.append("FROM ConcreteState ");
        queryBuilder.append("WHERE uid like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "uniqueStates");
    }

    // SELECT COUNT(*) AS totalActions FROM ConcreteAction WHERE NOT (`Desc` LIKE '%Invalid%') AND uid like X
    private int getTotalActions(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS totalActions ");
        queryBuilder.append("FROM ConcreteAction ");
        queryBuilder.append("WHERE NOT (`Desc` LIKE '%Invalid%') ");
        queryBuilder.append("AND in.uid like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "totalActions");
    }

    // SELECT 'uniqueActions' AS type, COUNT(*) AS count FROM (
    // SELECT DISTINCT Role, `Desc` FROM ConcreteAction WHERE NOT (`Desc` LIKE '%Invalid%') AND uid like X)
    private int getUniqueActions(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT 'uniqueActions' AS type, ");
        queryBuilder.append("COUNT(*) AS count FROM ( ");
        queryBuilder.append("SELECT DISTINCT Role, `Desc` ");
        queryBuilder.append("FROM ConcreteAction ");
        queryBuilder.append("WHERE NOT (`Desc` LIKE '%Invalid%') ");
        queryBuilder.append("AND in.uid like '" + modelIdentifier + "%' )");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "count");
    }

    // SELECT COUNT(*) AS invalidActions FROM Action WHERE `Desc` LIKE '%Invalid%' AND uid like
    private int getInvalidActions(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS invalidActions ");
        queryBuilder.append("FROM ConcreteAction ");
        queryBuilder.append("WHERE `Desc` LIKE '%Invalid%' ");
        queryBuilder.append("AND in.uid like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "invalidActions");
    }

    // SELECT COUNT(*) AS abstractStates FROM AbstractState AND uid like
    private int getAbstractStates(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS abstractStates ");
        queryBuilder.append("FROM AbstractState ");
        queryBuilder.append("WHERE modelIdentifier like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "abstractStates");
    }

    private boolean getTestGoalAccomplished(String modelIdentifier, StateModelManager stateModelManager,
                                            String searchMessage) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("FROM ConcreteState ");
        queryBuilder.append("WHERE uid LIKE '" + modelIdentifier + "%' ");
        queryBuilder.append("AND WebInnerHTML LIKE '%" + searchMessage + "%'");

        String query = queryBuilder.toString();
        String result = stateModelManager.queryStateModel(query);

        // Return true if the search message was found.
        return result.contains("COUNT(*): 1");
    }

    private int parseQueryResponse(String output, String field) {
        // The output I received looks like the following:
        // {
        // result: 0
        // }
        // This looks like JSON but is not valid JSON due to missing quotation marks so we use regex.

        String regex = field + ":\\s*(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);

        if (matcher.find()) {
            // Parse the matched group as an integer
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }

    @Override
    public void addMetrics(String modelIdentifier, StateModelManager stateModelManager) {
        StateModelMetrics newMetrics = new StateModelMetrics(modelIdentifier);
        newMetrics.setUniqueStates(getUniqueStates(modelIdentifier, stateModelManager));
        newMetrics.setTotalActions(getTotalActions(modelIdentifier, stateModelManager));
        newMetrics.setUniqueActions(getUniqueActions(modelIdentifier, stateModelManager));
        newMetrics.setInvalidActions(getInvalidActions(modelIdentifier, stateModelManager));
        newMetrics.setAbstractStates(getAbstractStates(modelIdentifier, stateModelManager));

        if(!Objects.equals(searchMessage, "")) {
            newMetrics.setTestGoalAccomplished(
                    getTestGoalAccomplished(modelIdentifier, stateModelManager, searchMessage));
        }

        metrics.add(newMetrics);

        logger.log(Level.INFO, gson.toJson(newMetrics));
        logger.log(Level.INFO, "Metrics: " + metrics.size());
    }

    @Override
    public void saveMetrics() {
        // TODO: Save to JSON, calculate metrics like average/min/max
    }

    @Override
    public void printMetrics() {
        // TODO
    }
}
