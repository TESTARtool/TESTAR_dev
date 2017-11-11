/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package nl.ou.testar.a11y.reporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

/**
 * The results of evaluating muliple success criteria
 * @author Davy Kager
 *
 */
public final class EvaluationResults implements Serializable {
	
	private static final long serialVersionUID = 4338993838674375390L;

	/**
	 * The severity of a warning
	 */
	public static final double SEVERITY_WARNING =
			Verdict.SEVERITY_MIN + ((Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / 10.0);
	// The severity of an error is computed, see getOverallVerdict().
	
	private final List<EvaluationResult> results = new ArrayList<>();
	
	private int passCount = 0, warningCount = 0, errorCount = 0;
	
	/**
	 * Constructs a new container for evaluation results
	 */
	public EvaluationResults() {}

	/**
	 * Add an evaluation result to the list of results
	 * @param result The result.
	 */
	public void add(EvaluationResult result) {
		results.add(Assert.notNull(result));
		switch (result.getType()) {
			case WARNING:
				warningCount++;
				return;
			case ERROR:
				errorCount++;
				return;
			case OK:
				passCount++;
				return;
			default:
				return;
		}
	}
	
	/**
	 * Get a list of all evaluation results
	 * @return The list of results.
	 */
	public List<EvaluationResult> getResults() {
		return Collections.unmodifiableList(results);
	}
	
	/**
	 * Get the total number of evaluation results
	 * @return The result count.
	 */
	public int getResultCount() {
		return results.size();
	}
	
	/**
	 * Get the total number of evaluation results that are passes
	 * @return The pass count.
	 */
	public int getPassCount() {
		return passCount;
	}
	
	/**
	 * Get the total number of evaluation results that are warnings
	 * @return The warning count.
	 */
	public int getWarningCount() {
		return warningCount;
	}
	
	/**
	 * Get the total number of evaluation results that are errors
	 * @return The error count.
	 */
	public int getErrorCount() {
		return errorCount;
	}
	
	/**
	 * Returns if at least one evaluation result is a violation (warning or error)
	 * @return Whether or not the results contain any violations.
	 */
	public boolean hasViolations() {
		return getResultCount() - passCount > 0;
	}
	
	/**
	 * Computes an overall Verdict from all evaluation results
	 * The severity will match that of the highest-level problem that was found.
	 * It will be the minimum severity if no problems were found.
	 * @return A Verdict.
	 */
	public Verdict getOverallVerdict() {
		double severity = Verdict.OK.severity();
		for (EvaluationResult result : results) {
			switch (result.getType()) {
			case WARNING:
				severity = Math.max(severity, SEVERITY_WARNING);
				continue;
			case ERROR:
				severity = Math.max(severity, result.getVerdictSeverity());
				continue;
			default:
				continue;
			}
		}
		return new Verdict(severity, "Accessibility evaluation");
	}
	
	@Override
	public String toString() {
		return "EvaluationResults: {" +
				getWarningCount() + " warnings, " +
				getErrorCount() + " errors, " +
				getResultCount() + " total}";
	}
	
}
