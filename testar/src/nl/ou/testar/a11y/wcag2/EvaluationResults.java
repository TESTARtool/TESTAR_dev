package nl.ou.testar.a11y.wcag2;

import java.util.ArrayList;
import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

/**
 * The results of evaluating muliple success criteria
 * @author Davy Kager
 *
 */
public class EvaluationResults {
	
	/**
	 * The severity of a general problem
	 */
	public static final double SEVERITY_GENERAL =
			Verdict.SEVERITY_MIN + ((Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / 20.0);
	
	/**
	 * The severity of a warning
	 */
	public static final double SEVERITY_WARNING =
			Verdict.SEVERITY_MIN + ((Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / 10.0);
	// The severity of an error is computed, see getOverallVerdict().
	
	private final List<EvaluationResult> results = new ArrayList<>();
	
	/**
	 * Constructs a new container for evaluation results
	 */
	EvaluationResults() {}

	/**
	 * Add an evaluation result to the list of results
	 * @param result The result.
	 */
	void add(EvaluationResult result) {
		results.add(Assert.notNull(result));
	}
	
	/**
	 * Get a list of all evaluation results
	 * @return The list of results.
	 */
	public List<EvaluationResult> getResults() {
		return results;
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
			case GENERAL:
				severity = Math.max(severity, SEVERITY_GENERAL);
				continue;
			case WARNING:
				severity = Math.max(severity, SEVERITY_WARNING);
				continue;
			case ERROR:
				severity = Math.max(severity,
						result.getSuccessCriterion().getVerdictSeverity());
				continue;
			default:
				continue;
			}
		}
		return new Verdict(severity, "Accessibility evaluation");
	}

}
