package nl.ou.testar.a11y.wcag2;

/**
 * The result of evaluating a success criterion
 * @author Davy Kager
 *
 */
public class EvaluationResult {
	
	public enum Type {
		/**
		 * No problem found
		 */
		OK,
		
		/**
		 * An error
		 * This is a definite problem that can be detected automatically.
		 */
		ERROR,
		
		/**
		 * A warning
		 * This is a potential problem that needs expert evaluation.
		 */
		WARNING,
		
		/**
		 * A general problem
		 * This is a potential problem that is not specific to one piece of code
		 * and that needs expert evaluation.
		 */
		GENERAL;
	}
	
	private final SuccessCriterion criterion;
	private final Type type;

	/**
	 * Constructs a new evaluation result
	 * @param criterion The associated success criterion.
	 * @param type The problem type.
	 */
	EvaluationResult(SuccessCriterion criterion, Type type) {
		this.criterion = criterion;
		this.type = type;
	}
	
	/**
	 * Gets the success criterion associated with this evaluation result
	 * @return The success criterion.
	 */
	public SuccessCriterion getSuccessCriterion() {
		return criterion;
	}
	
	/**
	 * Gets the problem type of this evaluation result
	 * @return The problem type.
	 */
	public Type getType() {
		return type;
	}

}
