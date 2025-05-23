/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.analysis.metric;

import com.google.gson.Gson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.LlmTestGoal;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Metrics collector used to retrieve metrics for tests using llm agents.
 */
public class LlmMetricsCollector implements IMetricsCollector {
    protected static final Logger logger = LogManager.getLogger();
    private Gson gson;

    private final List<LlmTestGoal> testGoals;

    private LlmMetrics metrics = new LlmMetrics();

    /**
     * Creates a new LlmMetricsCollector
     * @param testGoals List<LlmTestGoal> to determine if test goals were met.
     */
    public LlmMetricsCollector(List<LlmTestGoal> testGoals) {
        this.testGoals = testGoals;
        gson = new Gson();
    }

    @Override
    public void collectMetrics(String modelIdentifier, StateModelManager stateModelManager, int invalidActions) {
        BasicMetrics.SequenceMetrics newMetrics = new BasicMetrics.SequenceMetrics(modelIdentifier);
        newMetrics.setUniqueStates(getUniqueStates(modelIdentifier, stateModelManager));
        newMetrics.setTotalActions(getTotalActions(modelIdentifier, stateModelManager));
        newMetrics.setUniqueActions(getUniqueActions(modelIdentifier, stateModelManager));
        newMetrics.setAbstractStates(getAbstractStates(modelIdentifier, stateModelManager));

        // Invalid actions are not always properly recorded in the state model (since they don't change the state?)
        // The easiest method for now is to manually keep track of invalidActions (see LlmActionSelector)
        newMetrics.setInvalidActions(invalidActions);

        // Use the TestCondition evaluator of  to determine if the LlmTestGoals have been achieved
        boolean allGoalsAccomplished = testGoals.stream()
        		.flatMap(goal -> goal.getCompletionConditions().stream())
        		.allMatch(condition -> condition.evaluate(modelIdentifier, stateModelManager));

        newMetrics.setTestGoalAccomplished(allGoalsAccomplished);

        metrics.addSequenceMetrics(newMetrics);

        logger.log(Level.INFO, gson.toJson(newMetrics));
        logger.log(Level.INFO, "Metrics: " + metrics.getSequenceMetrics().size());
    }

    @Override
    public void finalizeMetrics() {
        List<BasicMetrics.SequenceMetrics> allSequences = metrics.getSequenceMetrics();

        List<BasicMetrics.SequenceMetrics> tgCompleted = metrics
                .getSequenceMetrics()
                .stream()
                .filter(BasicMetrics.SequenceMetrics::isTestGoalAccomplished)
                .collect(Collectors.toList());

        metrics.setTgCompleteCount(tgCompleted.size());

        // The following metrics are most interesting to calculate based on sequences that completed the test goal.

        int minConcreteStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getUniqueStates).min().orElse(-1);
        metrics.setMinConcreteStatesComplete(minConcreteStatesComplete);

        int maxConcreteStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getUniqueStates).max().orElse(-1);;
        metrics.setMaxConcreteStatesComplete(maxConcreteStatesComplete);

        double averageConcreteStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getUniqueStates).average().orElse(-1);
        metrics.setAverageConcreteStatesComplete(averageConcreteStatesComplete);

        int minAbstractStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getAbstractStates).min().orElse(-1);
        metrics.setMinAbstractStatesComplete(minAbstractStatesComplete);

        int maxAbstractStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getAbstractStates).max().orElse(-1);
        metrics.setMaxAbstractStatesComplete(maxAbstractStatesComplete);

        double averageAbstractStatesComplete = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getAbstractStates).average().orElse(-1);
        metrics.setAverageAbstractStatesComplete(averageAbstractStatesComplete);

        int minTotalActions = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getTotalActions).min().orElse(-1);
        metrics.setMinTotalActionsComplete(minTotalActions);

        int maxTotalActions = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getTotalActions).max().orElse(-1);
        metrics.setMaxTotalActionsComplete(maxTotalActions);

        double averageTotalActions = tgCompleted
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getTotalActions).average().orElse(-1);
        metrics.setAverageTotalActionsComplete(averageTotalActions);

        // These metrics are more interesting over ALL sequences to see how often the LLM "misbehaves".

        int minInvalidActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getInvalidActions).min().orElse(-1);
        metrics.setMinInvalidActions(minInvalidActions);

        int maxInvalidActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getInvalidActions).max().orElse(-1);
        metrics.setMaxInvalidActions(maxInvalidActions);

        double averageInvalidActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getInvalidActions).average().orElse(-1);
        metrics.setAverageInvalidActions(averageInvalidActions);

        // Aggregating the amount of unique actions doesn't tell us very much.
        // It is more interesting to calculate the amount of repeat actions to see how often the LLM repeats actions

        int minRepeatActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getRepeatActions).min().orElse(-1);
        metrics.setMinRepeatActions(minRepeatActions);

        int maxRepeatActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getRepeatActions).max().orElse(-1);
        metrics.setMaxRepeatActions(maxRepeatActions);

        double averageRepeatActions = allSequences
                .stream().mapToInt(BasicMetrics.SequenceMetrics::getRepeatActions).average().orElse(-1);
        metrics.setAverageRepeatActions(averageRepeatActions);
    }

    @Override
    public void printMetrics() {
        logger.log(Level.INFO, toString());
    }

    @Override
    public String toString() {
        int sequences = metrics.getSequenceMetrics().size();

        StringBuilder sb = new StringBuilder();

        sb.append("--------------------\n");
        sb.append("LLM Metrics Report - " + sequences + " sequences\n");
        sb.append("--------------------\n");
        sb.append("Times all test goals completed: " + metrics.getTgCompleteCount() + "\n");
        sb.append("--------------------\n");
        sb.append("For sequences that completed the test goal: \n\n");
        sb.append("Min concrete states: " + metrics.getMinConcreteStatesComplete() + "\n");
        sb.append("Max concrete states: " + metrics.getMaxConcreteStatesComplete() + "\n");
        sb.append("Avg concrete states: " + metrics.getAverageConcreteStatesComplete() + "\n");
        sb.append("Min abstract states: " + metrics.getMinAbstractStatesComplete() + "\n");
        sb.append("Max abstract states: " + metrics.getMaxAbstractStatesComplete() + "\n");
        sb.append("Avg abstract states: " + metrics.getAverageAbstractStatesComplete() + "\n");
        sb.append("Min total actions (excl invalid): " + metrics.getMinTotalActionsComplete() + "\n");
        sb.append("Max total actions (excl invalid): " + metrics.getMaxTotalActionsComplete() + "\n");
        sb.append("Avg total actions (excl invalid): " + metrics.getAverageTotalActionsComplete() + "\n");
        sb.append("--------------------\n");
        sb.append("For ALL sequences:\n\n");
        sb.append("Min invalid actions: " + metrics.getMinInvalidActions() + "\n");
        sb.append("Max invalid actions: " + metrics.getMaxInvalidActions() + "\n");
        sb.append("Avg invalid actions: " + metrics.getAverageInvalidActions() + "\n");
        sb.append("Min repeat actions: " + metrics.getMinRepeatActions() + "\n");
        sb.append("Max repeat actions: " + metrics.getMaxRepeatActions() + "\n");
        sb.append("Avg repeat actions: " + metrics.getAverageRepeatActions() + "\n");
        sb.append("--------------------\n");
        return sb.toString();
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

        return QueryHelper.parseCountQueryResponse(output, "uniqueStates");
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

        return QueryHelper.parseCountQueryResponse(output, "totalActions");
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

        return QueryHelper.parseCountQueryResponse(output, "count");
    }

    // Does not work since NOP actions are usually not recorded in the state model since they don't affect the state.
    // SELECT COUNT(*) AS invalidActions FROM Action WHERE `Desc` LIKE '%Invalid%' AND uid like
    /*private int getInvalidActions(int totalActions, Settings settings) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS invalidActions ");
        queryBuilder.append("FROM ConcreteAction ");
        queryBuilder.append("WHERE `Desc` LIKE '%Invalid%' ");
        queryBuilder.append("AND in.uid like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return parseQueryResponse(output, "invalidActions");
    }*/

    // SELECT COUNT(*) AS abstractStates FROM AbstractState AND uid like
    private int getAbstractStates(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS abstractStates ");
        queryBuilder.append("FROM AbstractState ");
        queryBuilder.append("WHERE modelIdentifier like '" + modelIdentifier + "%'");

        String query = queryBuilder.toString();
        String output = stateModelManager.queryStateModel(query);

        return QueryHelper.parseCountQueryResponse(output, "abstractStates");
    }
}
