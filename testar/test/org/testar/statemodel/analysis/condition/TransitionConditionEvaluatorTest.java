package org.testar.statemodel.analysis.condition;

import org.junit.Test;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.analysis.condition.StateModelTraceCondition.TraceOperator;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransitionConditionEvaluatorTest {

    @Test
    public void test_empty_goal_transition_condition() {
        String testGoal = "";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());
    }

    @Test
    public void test_null_goal_transition_condition() {
        String testGoal = null;
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertEquals("The number of identified transition conditions should be zero", 
                0, evaluator.getConditions().size());
    }

    @Test
    public void test_non_json_goal_is_not_loaded() {
        String testGoal = "OriginState: StateA\nAction: ClickButton\nTargetState: StateB";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertEquals("Only JSON StateModelTracePaths goals should be loaded.",
                0, evaluator.getConditions().size());
    }

    @Test
    public void test_json_trace_condition_loaded() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTraceJsonGoal());

        assertEquals("Exactly one ordered trace condition should be loaded from the JSON goal.",
                1, evaluator.getConditions().size());
        assertTrue(evaluator.getConditions().get(0) instanceof StateModelTraceCondition);

        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        assertEquals(2, condition.getTraces().size());
        assertEquals("Login Transition", condition.getTraces().get(0).getName());
        assertEquals(TraceOperator.CONTAINS,
                condition.getTraces().get(0).getOriginState().getInclude().get(0).getOperator());
        assertEquals(TraceOperator.EQUALS,
                condition.getTraces().get(0).getAction().getInclude().get(0).getOperator());
        assertEquals(TraceOperator.REGEX,
                condition.getTraces().get(1).getTargetState().getInclude().get(1).getOperator());
    }

    @Test
    public void test_json_trace_paths_condition_loaded() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTracePathsJsonGoal());

        assertEquals("Exactly one trace-path condition should be loaded from the JSON goal.",
                1, evaluator.getConditions().size());
        assertTrue(evaluator.getConditions().get(0) instanceof StateModelTraceCondition);

        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        assertEquals(2, condition.getTracePaths().size());
        assertEquals("First possible request loan path", condition.getTracePaths().get(0).getName());
        assertEquals(1, condition.getTracePaths().get(0).getTraces().size());
        assertEquals("Second possible request loan path", condition.getTracePaths().get(1).getName());
        assertEquals(2, condition.getTracePaths().get(1).getTraces().size());
    }

    @Test
    public void test_extract_action_selection_goal_removes_json_trace_block() {
        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(getTraceJsonGoal());

        assertTrue(actionSelectionGoal.contains("Given the login page is displayed"));
        assertTrue(actionSelectionGoal.contains("When I log in with the username \"john\" and password \"demo\""));
        assertFalse(actionSelectionGoal.contains("StateModelTracePaths"));
        assertFalse(actionSelectionGoal.contains("Login Transition"));
        assertFalse(actionSelectionGoal.contains("\"originState\""));
    }

    @Test
    public void test_extract_action_selection_goal_removes_json_trace_paths_block() {
        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(getTracePathsJsonGoal());

        assertTrue(actionSelectionGoal.contains("Given the user requests a loan"));
        assertFalse(actionSelectionGoal.contains("StateModelTracePaths"));
        assertFalse(actionSelectionGoal.contains("First possible request loan path"));
        assertFalse(actionSelectionGoal.contains("\"traces\""));
    }

    @Test
    public void test_inline_escaped_newline_goal_loads_json_trace_condition() {
        String testGoal = getTraceJsonGoal().replace("\n", "\\n");

        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertEquals("Escaped inline LlmTestGoals content should load the StateModelTracePaths JSON block.",
                1, evaluator.getConditions().size());
    }

    @Test
    public void test_inline_escaped_newline_goal_strips_json_from_action_selection_goal() {
        String testGoal = getTraceJsonGoal().replace("\n", "\\n");

        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(testGoal);

        assertTrue(actionSelectionGoal.contains("Given the login page is displayed"));
        assertFalse(actionSelectionGoal.contains("\\n"));
        assertFalse(actionSelectionGoal.contains("StateModelTracePaths"));
        assertFalse(actionSelectionGoal.contains("\"originState\""));
    }

    @Test
    public void test_extract_action_selection_goal_keeps_non_json_goal() {
        String testGoal = "Log in with the username john and the password demo";

        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(testGoal);

        assertEquals(testGoal, actionSelectionGoal);
    }

    @Test
    public void test_extract_action_selection_goal_keeps_goal_with_non_json_braces() {
        String testGoal = "Given I type {test}";

        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(testGoal);
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertEquals(testGoal, actionSelectionGoal);
        assertEquals(0, evaluator.getConditions().size());
    }

    @Test
    public void test_extract_action_selection_goal_ignores_non_json_braces_before_trace_json() {
        String testGoal = "Given I type {test}\n" + getTraceJsonGoal();

        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(testGoal);
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        assertTrue(actionSelectionGoal.contains("Given I type {test}"));
        assertTrue(actionSelectionGoal.contains("Given the login page is displayed"));
        assertFalse(actionSelectionGoal.contains("StateModelTracePaths"));
        assertEquals(1, evaluator.getConditions().size());
    }

    @Test
    public void test_extract_action_selection_goal_handles_null_goal() {
        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(null);

        assertEquals("", actionSelectionGoal);
    }

    @Test
    public void test_extract_action_selection_goal_keeps_text_after_json_block() {
        String testGoal = "Before\n{\"StateModelTracePaths\": []}\nAfter";

        String actionSelectionGoal = TransitionConditionEvaluator.extractActionSelectionGoal(testGoal);

        assertEquals("Before\n\nAfter", actionSelectionGoal);
    }

    @Test
    public void test_json_trace_condition_supports_default_contains_operator() {
        String testGoal = "{\n" +
                "  \"StateModelTracePaths\": [\n" +
                "    {\n" +
                "      \"name\": \"Single path\",\n" +
                "      \"traces\": [\n" +
                "        {\n" +
                "          \"name\": \"Default Operator\",\n" +
                "          \"originState\": { \"include\": [ { \"tag\": \"WebInnerHTML\", \"value\": \"Customer Login\" } ] },\n" +
                "          \"action\": { \"include\": [ { \"tag\": \"Desc\", \"value\": \"Log In\" } ] },\n" +
                "          \"targetState\": { \"include\": [ { \"tag\": \"WebInnerHTML\", \"value\": \"Welcome\" } ] }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);

        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        assertEquals(TraceOperator.CONTAINS,
                condition.getTraces().get(0).getAction().getInclude().get(0).getOperator());
    }

    @Test
    public void test_json_trace_condition_builds_include_exclude_query() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTraceJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        String query = condition.buildTraceQuery(condition.getTraces().get(0), "model");

        assertTrue(query.contains("FROM ConcreteAction"));
        assertTrue(query.contains("out.uid LIKE \"model%\""));
        assertTrue(query.contains("in.uid LIKE \"model%\""));
        assertTrue(query.contains("out.WebInnerHTML containstext \"Customer Login\""));
        assertTrue(query.contains("`Desc` = \"Left Click at 'input_log_in'\""));
        assertTrue(query.contains("in.WebInnerHTML containstext \"Welcome John Smith\""));
        assertTrue(query.contains("AND NOT (in.WebInnerHTML containstext \"Customer Login\")"));
    }

    @Test
    public void test_json_trace_condition_builds_regex_query() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTraceJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        String query = condition.buildTraceQuery(condition.getTraces().get(1), "model");

        assertTrue(query.contains("`Desc` LIKE \"%Apply Now%\""));
        assertTrue(query.contains("in.WebInnerHTML MATCHES \".*cannot grant a loan in that amount.*\""));
        assertTrue(query.contains("AND NOT (in.WebInnerHTML containstext \"Approved\")"));
    }

    @Test
    public void test_json_trace_condition_does_not_backslash_escape_like_underscore() {
        String testGoal = "{\n" +
                "  \"StateModelTracePaths\": [\n" +
                "    {\n" +
                "      \"name\": \"Single path\",\n" +
                "      \"traces\": [\n" +
                "        {\n" +
                "          \"name\": \"Input Action\",\n" +
                "          \"action\": { \"include\": [ { \"tag\": \"Desc\", \"value\": \"type input_field\" } ] }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(testGoal);
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);

        String query = condition.buildTraceQuery(condition.getTraces().get(0), "model");

        assertTrue(query.contains("`Desc` LIKE \"%type input_field%\""));
        assertFalse(query.contains("input\\_field"));
    }

    @Test
    public void test_json_trace_condition_acknowledges_multiple_traces_in_same_evaluation() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTraceJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);
        StateModelManager stateModelManager = mock(StateModelManager.class);
        when(stateModelManager.queryStateModel(anyString())).thenReturn("{found: 1}", "{found: 1}");

        boolean result = condition.evaluate("model", stateModelManager);

        assertTrue(result);
        assertEquals(2, condition.getCurrentTraceIndex());
        verify(stateModelManager, times(2)).queryStateModel(anyString());
    }

    @Test
    public void test_json_trace_condition_continues_from_second_trace_if_second_is_pending() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTraceJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);
        StateModelManager stateModelManager = mock(StateModelManager.class);
        when(stateModelManager.queryStateModel(anyString())).thenReturn("{found: 1}", "{found: 0}", "{found: 1}");

        assertFalse(condition.evaluate("model", stateModelManager));
        assertEquals(1, condition.getCurrentTraceIndex());

        assertTrue(condition.evaluate("model", stateModelManager));
        assertEquals(2, condition.getCurrentTraceIndex());
        verify(stateModelManager, times(3)).queryStateModel(anyString());
    }

    @Test
    public void test_json_trace_paths_condition_completes_when_first_path_completes() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTracePathsJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);
        StateModelManager stateModelManager = mock(StateModelManager.class);
        when(stateModelManager.queryStateModel(anyString())).thenReturn("{found: 1}");

        boolean result = condition.evaluate("model", stateModelManager);

        assertTrue(result);
        assertEquals(1, condition.getTracePaths().get(0).getCurrentTraceIndex());
        assertEquals(0, condition.getTracePaths().get(1).getCurrentTraceIndex());
        verify(stateModelManager, times(1)).queryStateModel(anyString());
    }

    @Test
    public void test_json_trace_paths_condition_checks_second_path_when_first_is_pending() {
        TransitionConditionEvaluator evaluator = new TransitionConditionEvaluator(getTracePathsJsonGoal());
        StateModelTraceCondition condition = (StateModelTraceCondition)evaluator.getConditions().get(0);
        StateModelManager stateModelManager = mock(StateModelManager.class);
        when(stateModelManager.queryStateModel(anyString())).thenReturn(
                "{found: 0}",
                "{found: 1}",
                "{found: 0}",
                "{found: 0}",
                "{found: 1}");

        assertFalse(condition.evaluate("model", stateModelManager));
        assertEquals(0, condition.getTracePaths().get(0).getCurrentTraceIndex());
        assertEquals(1, condition.getTracePaths().get(1).getCurrentTraceIndex());

        assertTrue(condition.evaluate("model", stateModelManager));
        assertEquals(0, condition.getTracePaths().get(0).getCurrentTraceIndex());
        assertEquals(2, condition.getTracePaths().get(1).getCurrentTraceIndex());
        verify(stateModelManager, times(5)).queryStateModel(anyString());
    }

    private String getTraceJsonGoal() {
        return "Given the login page is displayed\n" +
                "When I log in with the username \"john\" and password \"demo\"\n" +
                "{\n" +
                "  \"StateModelTracePaths\": [\n" +
                "    {\n" +
                "      \"name\": \"Login and denied loan path\",\n" +
                "      \"traces\": [\n" +
                "        {\n" +
                "          \"name\": \"Login Transition\",\n" +
                "          \"originState\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Customer Login\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": []\n" +
                "          },\n" +
                "          \"action\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"Desc\",\n" +
                "                \"operator\": \"equals\",\n" +
                "                \"value\": \"Left Click at 'input_log_in'\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": []\n" +
                "          },\n" +
                "          \"targetState\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Welcome John Smith\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Customer Login\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Denied Loan Transition\",\n" +
                "          \"originState\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Apply for a Loan\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": []\n" +
                "          },\n" +
                "          \"action\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"Desc\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Apply Now\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": []\n" +
                "          },\n" +
                "          \"targetState\": {\n" +
                "            \"include\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Denied\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"regex\",\n" +
                "                \"value\": \".*cannot grant a loan in that amount.*\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"exclude\": [\n" +
                "              {\n" +
                "                \"tag\": \"WebInnerHTML\",\n" +
                "                \"operator\": \"contains\",\n" +
                "                \"value\": \"Approved\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private String getTracePathsJsonGoal() {
        return "Given the user requests a loan\n" +
                "{\n" +
                "  \"StateModelTracePaths\": [\n" +
                "    {\n" +
                "      \"name\": \"First possible request loan path\",\n" +
                "      \"traces\": [\n" +
                "        {\n" +
                "          \"name\": \"Denied Loan Transition\",\n" +
                "          \"originState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"Apply for a Loan\" } ], \"exclude\": [] },\n" +
                "          \"action\": { \"include\": [ { \"tag\": \"Desc\", \"operator\": \"contains\", \"value\": \"input_apply_now\" } ], \"exclude\": [] },\n" +
                "          \"targetState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"We cannot grant a loan in that amount with your available funds.\" } ], \"exclude\": [] }\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Second possible request loan path\",\n" +
                "      \"traces\": [\n" +
                "        {\n" +
                "          \"name\": \"Login Transition\",\n" +
                "          \"originState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"Customer Login\" } ], \"exclude\": [] },\n" +
                "          \"action\": { \"include\": [ { \"tag\": \"Desc\", \"operator\": \"contains\", \"value\": \"input_log_in\" } ], \"exclude\": [] },\n" +
                "          \"targetState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"Welcome John Smith\" } ], \"exclude\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"Customer Login\" } ] }\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Denied Loan Transition\",\n" +
                "          \"originState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"Apply for a Loan\" } ], \"exclude\": [] },\n" +
                "          \"action\": { \"include\": [ { \"tag\": \"Desc\", \"operator\": \"contains\", \"value\": \"input_apply_now\" } ], \"exclude\": [] },\n" +
                "          \"targetState\": { \"include\": [ { \"tag\": \"WebInnerText\", \"operator\": \"contains\", \"value\": \"We cannot grant a loan in that amount with your available funds\" } ], \"exclude\": [] }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
