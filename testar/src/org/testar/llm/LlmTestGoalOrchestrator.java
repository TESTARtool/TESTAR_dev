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

package org.testar.llm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Verdict;

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
