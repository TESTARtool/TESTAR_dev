/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
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

package org.testar.statemodel.analysis.condition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.testar.monkey.alayer.State;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transition condition that acknowledges ordered traces and alternative trace paths.
 */
public class StateModelTraceCondition extends TestCondition {
    private static final Logger logger = LogManager.getLogger();

    private final List<StateModelTracePath> tracePaths;

    public static StateModelTraceCondition fromTracePaths(List<StateModelTracePath> tracePaths,
                                                          ConditionComparator comparator,
                                                          int threshold) {
        return new StateModelTraceCondition(tracePaths, comparator, threshold);
    }

    private StateModelTraceCondition(List<StateModelTracePath> tracePaths,
                                     ConditionComparator comparator,
                                     int threshold) {
        super(comparator, threshold);
        this.tracePaths = tracePaths == null ? Collections.emptyList() : new ArrayList<>(tracePaths);
    }

    public List<StateModelTrace> getTraces() {
        if (tracePaths.isEmpty()) {
            return Collections.emptyList();
        }

        return tracePaths.get(0).getTraces();
    }

    public List<StateModelTracePath> getTracePaths() {
        return Collections.unmodifiableList(tracePaths);
    }

    public int getCurrentTraceIndex() {
        if (tracePaths.isEmpty()) {
            return 0;
        }

        return tracePaths.get(0).getCurrentTraceIndex();
    }

    @Override
    public boolean evaluate(String modelIdentifier, StateModelManager stateModelManager) {
        for (StateModelTracePath tracePath : tracePaths) {
            if (advanceTracePath(tracePath, modelIdentifier, stateModelManager)) {
                return true;
            }
        }

        return tracePaths.isEmpty();
    }

    @Override
    public boolean evaluate(State state) {
        for (StateModelTracePath tracePath : tracePaths) {
            if (tracePath.isComplete()) {
                return true;
            }
        }

        return tracePaths.isEmpty();
    }

    private boolean advanceTracePath(StateModelTracePath tracePath,
                                     String modelIdentifier,
                                     StateModelManager stateModelManager) {
        // A single model check can acknowledge several already-satisfied traces in one path.
        while (!tracePath.isComplete()) {
            StateModelTrace trace = tracePath.getCurrentTrace();

            if (!evaluateTrace(trace, modelIdentifier, stateModelManager)) {
                return false;
            }

            tracePath.acknowledgeCurrentTrace();
            logger.info(String.format("State model trace completed in path '%s': %s", tracePath.getName(), trace.getName()));
        }

        return true;
    }

    private boolean evaluateTrace(StateModelTrace trace, String modelIdentifier, StateModelManager stateModelManager) {
        String query = buildTraceQuery(trace, modelIdentifier);
        String result = stateModelManager.queryStateModel(query);

        int matches = QueryHelper.parseCountQueryResponse(result, "found");
        int threshold = getThreshold();

        switch(getComparator()) {
            case EQUAL:
                return matches == threshold;
            case LESS_THAN:
                return matches < threshold;
            case GREATER_THAN:
                return matches > threshold;
            case LESS_THAN_EQUALS:
                return matches <= threshold;
            case GREATER_THAN_EQUALS:
                return matches >= threshold;
            default:
                throw new InvalidArgumentException("Invalid comparator for condition!");
        }
    }

    String buildTraceQuery(StateModelTrace trace, String modelIdentifier) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS found ");
        queryBuilder.append("FROM ConcreteAction ");
        queryBuilder.append("WHERE out.uid LIKE ");
        appendStringLiteral(queryBuilder, modelIdentifier + "%");
        queryBuilder.append(" ");
        queryBuilder.append("AND in.uid LIKE ");
        appendStringLiteral(queryBuilder, modelIdentifier + "%");

        // ConcreteAction links the origin state as out and the target state as in.
        appendCriteria(queryBuilder, "out", trace.getOriginState());
        appendCriteria(queryBuilder, null, trace.getAction());
        appendCriteria(queryBuilder, "in", trace.getTargetState());

        return queryBuilder.toString();
    }

    private void appendCriteria(StringBuilder queryBuilder, String vertexPrefix, TraceCriteria criteria) {
        if (criteria == null) {
            return;
        }

        for (TraceCriterion criterion : criteria.getInclude()) {
            queryBuilder.append(" AND ");
            appendCriterion(queryBuilder, vertexPrefix, criterion);
        }

        for (TraceCriterion criterion : criteria.getExclude()) {
            queryBuilder.append(" AND NOT (");
            appendCriterion(queryBuilder, vertexPrefix, criterion);
            queryBuilder.append(")");
        }
    }

    private void appendCriterion(StringBuilder queryBuilder, String vertexPrefix, TraceCriterion criterion) {
        String field = formatField(vertexPrefix, criterion.getTag());
        String value = criterion.getValue();
        TraceOperator operator = criterion.getOperator();

        switch(operator) {
            case EQUALS:
                queryBuilder.append(field);
                queryBuilder.append(" = ");
                appendStringLiteral(queryBuilder, value);
                break;
            case REGEX:
                queryBuilder.append(field);
                queryBuilder.append(" MATCHES ");
                appendStringLiteral(queryBuilder, value);
                break;
            case CONTAINS:
            default:
                // Action fields use LIKE; state fields use OrientDB full-text containstext.
                if (vertexPrefix == null) {
                    queryBuilder.append(field);
                    queryBuilder.append(" LIKE ");
                    appendStringLiteral(queryBuilder, "%" + nullSafe(value) + "%");
                } else {
                    queryBuilder.append(field);
                    queryBuilder.append(" containstext ");
                    appendStringLiteral(queryBuilder, value);
                }
                break;
        }
    }

    private String formatField(String vertexPrefix, String tag) {
        if (vertexPrefix == null) {
            return "`" + tag + "`";
        }

        return vertexPrefix + "." + tag;
    }

    private void appendStringLiteral(StringBuilder queryBuilder, String value) {
        queryBuilder.append("\"");
        queryBuilder.append(escapeStringLiteral(value));
        queryBuilder.append("\"");
    }

    private String escapeStringLiteral(String value) {
        return nullSafe(value).replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String nullSafe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    /**
     * One possible ordered route to complete the transition condition.
     */
    public static class StateModelTracePath {
        private final String name;
        private final List<StateModelTrace> traces;
        private int currentTraceIndex;

        public StateModelTracePath(String name, List<StateModelTrace> traces) {
            this.name = name;
            this.traces = traces == null ? Collections.emptyList() : new ArrayList<>(traces);
            this.currentTraceIndex = 0;
        }

        public String getName() {
            return name == null || name.isBlank() ? "Unnamed trace path" : name;
        }

        public List<StateModelTrace> getTraces() {
            return Collections.unmodifiableList(traces);
        }

        public int getCurrentTraceIndex() {
            return currentTraceIndex;
        }

        private boolean isComplete() {
            return currentTraceIndex >= traces.size();
        }

        private StateModelTrace getCurrentTrace() {
            return traces.get(currentTraceIndex);
        }

        private void acknowledgeCurrentTrace() {
            currentTraceIndex++;
        }
    }

    /**
     * One expected state-model transition.
     */
    public static class StateModelTrace {
        private final String name;
        private final TraceCriteria originState;
        private final TraceCriteria action;
        private final TraceCriteria targetState;

        public StateModelTrace(String name, TraceCriteria originState, TraceCriteria action, TraceCriteria targetState) {
            this.name = name;
            this.originState = originState == null ? new TraceCriteria(null, null) : originState;
            this.action = action == null ? new TraceCriteria(null, null) : action;
            this.targetState = targetState == null ? new TraceCriteria(null, null) : targetState;
        }

        public String getName() {
            return name == null || name.isBlank() ? "Unnamed trace" : name;
        }

        public TraceCriteria getOriginState() {
            return originState;
        }

        public TraceCriteria getAction() {
            return action;
        }

        public TraceCriteria getTargetState() {
            return targetState;
        }
    }

    /**
     * Criteria that must be present or absent for one transition part.
     */
    public static class TraceCriteria {
        private final List<TraceCriterion> include;
        private final List<TraceCriterion> exclude;

        public TraceCriteria(List<TraceCriterion> include, List<TraceCriterion> exclude) {
            this.include = include == null ? Collections.emptyList() : new ArrayList<>(include);
            this.exclude = exclude == null ? Collections.emptyList() : new ArrayList<>(exclude);
        }

        public List<TraceCriterion> getInclude() {
            return Collections.unmodifiableList(include);
        }

        public List<TraceCriterion> getExclude() {
            return Collections.unmodifiableList(exclude);
        }
    }

    /**
     * One tag/value predicate inside an include or exclude block.
     */
    public static class TraceCriterion {
        private final String tag;
        private final TraceOperator operator;
        private final String value;

        public TraceCriterion(String tag, TraceOperator operator, String value) {
            this.tag = tag;
            this.operator = operator == null ? TraceOperator.CONTAINS : operator;
            this.value = value;
        }

        public String getTag() {
            return tag;
        }

        public TraceOperator getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }
    }

    public enum TraceOperator {
        CONTAINS,
        EQUALS,
        REGEX
    }
}
