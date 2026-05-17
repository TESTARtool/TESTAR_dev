package org.testar.llm;

import org.junit.Test;
import org.testar.monkey.alayer.Verdict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestLlmTestGoalOrchestrator {

	@Test
	public void testGoalOrchestratorStartSequence() {
		List<String> activatedGoals = new ArrayList<>();
		AtomicBoolean appendPrevious = new AtomicBoolean(true);

		LlmTestGoalOrchestrator orchestrator = new LlmTestGoalOrchestrator(
				Arrays.asList(goal("goal-1"), goal("goal-2")),
				(goal, append) -> {
					activatedGoals.add(goal.getTestGoal());
					appendPrevious.set(append);
				}
		);

		orchestrator.startSequence();

		assertEquals(1, activatedGoals.size());
		assertEquals("goal-1", activatedGoals.get(0));
		assertFalse(appendPrevious.get());
	}

	@Test
	public void testGoalOrchestratorCompletionAdvancesToNextGoal() {
		AtomicInteger activations = new AtomicInteger(0);
		AtomicBoolean appendPrevious = new AtomicBoolean(false);

		LlmTestGoalOrchestrator orchestrator = new LlmTestGoalOrchestrator(
				Arrays.asList(goal("goal-1"), goal("goal-2")),
				(goal, append) -> {
					activations.incrementAndGet();
					appendPrevious.set(append);
				}
		);
		orchestrator.startSequence();

		List<Verdict> stopVerdicts = orchestrator.processGoalVerdicts(
				Collections.singletonList(new Verdict(Verdict.Severity.LLM_COMPLETE, "done"))
		);

		assertTrue(stopVerdicts.isEmpty());
		assertEquals(2, activations.get());
		assertTrue(appendPrevious.get());
		assertEquals("goal-2", orchestrator.getCurrentGoal().getTestGoal());
	}

	@Test
	public void testGoalOrchestratorCompletionFinalGoalStops() {
		LlmTestGoalOrchestrator orchestrator = new LlmTestGoalOrchestrator(
				Collections.singletonList(goal("goal-1")),
				(goal, append) -> {}
		);
		orchestrator.startSequence();

		List<Verdict> stopVerdicts = orchestrator.processGoalVerdicts(
				Collections.singletonList(new Verdict(Verdict.Severity.CONDITION_COMPLETE, "all goals done"))
		);

		assertNotNull(stopVerdicts);
		assertEquals(1, stopVerdicts.size());
		assertEquals(Verdict.Severity.CONDITION_COMPLETE.getValue(), stopVerdicts.get(0).severity(), 0.0);
	}

	@Test
	public void testGoalOrchestratorInvalidVerdict() {
		LlmTestGoalOrchestrator orchestrator = new LlmTestGoalOrchestrator(
				Collections.singletonList(goal("goal-1")),
				(goal, append) -> {}
		);
		orchestrator.startSequence();

		List<Verdict> stopVerdicts = orchestrator.processGoalVerdicts(
				Collections.singletonList(new Verdict(Verdict.Severity.LLM_INVALID, "invalid behavior"))
		);

		assertNotNull(stopVerdicts);
		assertEquals(1, stopVerdicts.size());
		assertEquals(Verdict.Severity.LLM_INVALID.getValue(), stopVerdicts.get(0).severity(), 0.0);
	}

	private static LlmTestGoal goal(String value) {
		return new LlmTestGoal(value, Collections.emptyList());
	}
}
