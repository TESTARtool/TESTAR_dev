/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.verdict.Verdict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

/**
 * Coordinates multi-goal progression and stop conditions for LLM goal-oriented protocols.
 */
public class LlmTestGoalOrchestrator {
	static final Logger logger = LogManager.getLogger();

	private final List<LlmTestGoal> configuredGoals;
	private final BiConsumer<LlmTestGoal, Boolean> goalActivationHandler;

	private Queue<LlmTestGoal> goalQueue = new LinkedList<>();
	private LlmTestGoal currentGoal;

	public LlmTestGoalOrchestrator(List<LlmTestGoal> goals, BiConsumer<LlmTestGoal, Boolean> goalActivationHandler) {
		this.configuredGoals = goals == null ? Collections.emptyList() : new ArrayList<>(goals);
		this.goalActivationHandler = goalActivationHandler;
	}

	public void startSequence() {
		goalQueue = new LinkedList<>(configuredGoals);
		currentGoal = goalQueue.poll();
		activateCurrentGoal(false);
	}

	public LlmTestGoal getCurrentGoal() {
		return currentGoal;
	}

	/**
	 * Processes goal evaluator verdicts (LLM or condition-based) and updates progression.
	 *
	 * @param goalVerdicts verdicts produced by a goal evaluator
	 * @return test goal verdicts if the sequence should stop, empty list otherwise
	 */
	public List<Verdict> processGoalVerdicts(List<Verdict> goalVerdicts) {
		if (goalVerdicts == null || goalVerdicts.isEmpty()) {
			return Collections.emptyList();
		}

		for (Verdict verdict : goalVerdicts) {
			if (verdict == null) {
				continue;
			}

			if (verdict.severity() == Verdict.Severity.LLM_INVALID.getValue()) {
				logger.info("LLM detected invalid behavior, stopping test sequence.");
				return Collections.singletonList(verdict);
			}

			if (verdict.isCompletion()) {
				currentGoal = goalQueue.poll();
				if (currentGoal == null) {
					logger.info("Test goal completed, but no more test goals.");
					return Collections.singletonList(verdict);
				}
				logger.info("Test goal completed, moving to next test goal.");
				activateCurrentGoal(true);
			}
		}

		return Collections.emptyList();
	}

	private void activateCurrentGoal(boolean appendPreviousGoal) {
		if (currentGoal == null || goalActivationHandler == null) {
			return;
		}
		goalActivationHandler.accept(currentGoal, appendPreviousGoal);
	}
}
