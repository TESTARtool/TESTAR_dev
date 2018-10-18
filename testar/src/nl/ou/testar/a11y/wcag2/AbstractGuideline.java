/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

package nl.ou.testar.a11y.wcag2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.ou.testar.GraphDB;
import nl.ou.testar.a11y.protocols.Evaluator;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
/**
 * An abstract WCAG guideline
 * Subclasses implement specific guideline behavior.
 * @author Davy Kager
 *
 */
public abstract class AbstractGuideline extends ItemBase implements Evaluator {
	
	private static final long serialVersionUID = -2941524827644792263L;
	
	/**
	 * The list of all the success criteria in this guideline.
	 */
	protected final List<SuccessCriterion> criteria = new ArrayList<>();
	
	/**
	 * Constructs a new guideline.
	 * @param nr The number of the guideline.
	 * @param name The name of the guideline.
	 * @param parent The principle (parent) this guideline belongs to.
	 */
	protected AbstractGuideline(int nr, String name, AbstractPrinciple parent) {
		super(nr, name, Assert.notNull(parent));
	}
	
	/**
	 * Gets all success criteria in this guideline.
	 * @return The list of success criteria.
	 */
	public List<SuccessCriterion> getSuccessCriteria() {
		return Collections.unmodifiableList(criteria);
	}
	
	/**
	 * Gets a success criterion by its name (case-insensitive).
	 * @param name The name.
	 * @return The success criterion, or null if not found.
	 */
	protected SuccessCriterion getSuccessCriterionByName(String name) {
		Assert.notNull(name);
		for (SuccessCriterion criterion : criteria) {
			if (criterion.getName().equalsIgnoreCase(name)) {
				return criterion;
			}
		}
		return null;
	}
	
	/**
	 * Evaluates the accessibility of the given state.
	 * This will include zero or more evaluation results for each success criterion in this guideline.
	 * This method executes oracles in on-the-fly evaluation.
	 * @param widgets The widgets to consider.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		return new EvaluationResults();
	}
	
	/**
	 * Derives the possible actions from the given state.
	 * This will include zero or more actions for each success criterion in this guideline.
	 * The actions are specific to accessibility.
	 * This method derives actions in on-the-fly evaluation.
	 * @param widgets The widgets to consider.
	 * @return The set of actions.
	 */
	@Override
	public Set<Action> deriveActions(List<Widget> widgets) {
		return new HashSet<>();
	}
	
	/**
	 * Evaluates the overall accessibility of the SUT by querying the given graph database.
	 * This will include zero or more evaluation results for each success criterion in this guideline.
	 * This method executes oracles in offline evaluation.
	 * @param graphDB The graph database.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults query(GraphDB graphDB) {
		return new EvaluationResults();
	}
	
	@Override
	public String getImplementationVersion() {
		return "WCAG2ICT-guideline-" + WCAG2ICT.VERSION;
	}
	
	/**
	 * Constructs a new EvaluationResult indicating that the given success criterion was passed.
	 * @param criterion The success criterion.
	 * @return A new EvaluationResult.
	 */
	protected WCAG2EvaluationResult evaluationPassed(SuccessCriterion criterion) {
		return new WCAG2EvaluationResult(criterion, WCAG2EvaluationResult.Type.OK,
				"Evaluation passed");
	}
	
}
