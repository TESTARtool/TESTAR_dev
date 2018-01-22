/*************************************************************************************
 *
 * COPYRIGHT (2017):
 *
 * Open Universiteit
 * www.ou.nl<http://www.ou.nl>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
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
 ************************************************************************************/

package nl.ou.testar.a11y.wcag2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
import nl.ou.testar.a11y.protocols.Evaluator;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * Specification of WCAG 2.0 according to WCAG2ICT
 * @author Davy Kager
 *
 */
public final class WCAG2ICT implements Evaluator {
	
	/**
	 * The implementation version
	 */
	static final String VERSION = "20171121";
	
	/**
	 * The base part for anchor links, e.g. to success criteria
	 */
	
	private final List<AbstractPrinciple> principles = new ArrayList<>();
	
	/**
	 * Constructs the WCAG 2.0 specification
	 */
	public WCAG2ICT() {
		principles.add(new PerceivablePrinciple());
		principles.add(new OperablePrinciple());
		principles.add(new UnderstandablePrinciple());
		principles.add(new RobustPrinciple());
		setActionTags();
	}
	
	/**
	 * Gets all principles in WCAG2ICT
	 * @return The list of principles.
	 */
	public List<AbstractPrinciple> getPrinciples() {
		return Collections.unmodifiableList(principles);
	}
	
	/**
	 * Evaluates the accessibility of the given state
	 * This will collect evaluation results from all principles in WCAG2ICT.
	 * This method executes oracles in on-the-fly evaluation.
	 * @param widgets The widgets to consider.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.evaluate(widgets).getResults())
				results.add(result);
		return results;
	}
	
	/**
	 * Derives the possible actions from the given state
	 * This will collect actions from all principles in WCAG2ICT.
	 * The actions are specific to accessibility.
	 * This method derives actions in on-the-fly evaluation.
	 * @param widgets The widgets to consider.
	 * @return The set of actions.
	 */
	@Override
	public Set<Action> deriveActions(List<Widget> widgets) {
		Set<Action> actions = new HashSet<>();
		for (AbstractPrinciple p : principles)
			actions.addAll(p.deriveActions(widgets));
		return actions;
	}
	
	/**
	 * Evaluates the overall accessibility of the SUT by querying the given graph database
	 * This will collect evaluation results from all principles in WCAG2ICT.
	 * This method executes oracles in offline evaluation.
	 * @param graphDB The graph database.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults query(GraphDB graphDB) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.query(graphDB).getResults())
				results.add(result);
		return results;
	}
	
	@Override
	public String getImplementationVersion() {
		return "WCAG2ICT-" + VERSION;
	}
	
	private void setActionTags() {
		AccessibilityUtil.AC_NAVIGATE_PREVIOUS_WIDGET.set(WCAG2Tags.WCAG2IsInWindowNavigation, true);
		AccessibilityUtil.AC_NAVIGATE_NEXT_WIDGET.set(WCAG2Tags.WCAG2IsInWindowNavigation, true);
	}
	
}
