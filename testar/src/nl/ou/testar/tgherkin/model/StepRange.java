package nl.ou.testar.tgherkin.model;

import java.util.List;
import java.util.Random;

/**
 * Tgherkin Step.
 *
 */
public class StepRange extends Step {

	private final int fromRange;
	private final int toRange;
	private int targetedActions;
	private int currentAction;
	
	/**
     * StepRange constructor.
     * @param title title
     * @param fromRange given from range
     * @param toRange given to range
     * @param givenCondition given widget tree condition
     * @param whenGestures list of conditional gestures
     * @param thenCondition then widget tree condition
     */
    public StepRange(String title, int fromRange, int toRange, WidgetTreeCondition givenCondition, List<ConditionalGesture> whenGestures, WidgetTreeCondition thenCondition) {
        super(title, givenCondition, whenGestures, thenCondition);
    	this.fromRange = fromRange;
    	this.toRange = toRange;
    }

    /**
     * Retrieve from range.
     * @return from range
     */
    public int getFromRange() {
		return fromRange;
	}

    /**
     * Retrieve to range.
     * @return to range
     */
	public int getToRange() {
		return toRange;
	}

	/**	  
	 * Evaluate given condition.
	 * @param proxy given protocol proxy
	 * @param dataTable given data table
	 * @param mismatchOccurred indicator whether a mismatch occurred
	 * @return  true if given condition is applicable, otherwise false 
	 */
	@Override
	public boolean evaluateGivenCondition(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		// reset status
		setStatus(Status.UNDETERMINED);
		return super.evaluateGivenCondition(proxy, dataTable, mismatchOccurred);
	}
	
	
	/**
     * Check.
     * @param dataTable given data table
     * @return list of error descriptions
     */
	@Override
	public List<String> check(DataTable dataTable) {
		List<String> list = super.check(dataTable);
		if (fromRange > toRange) {
			list.add("Validation error - invalid range for step " + getTitle() + ": from range " + fromRange + " greater then To range " + toRange + "\n");
		}
		return list;
	}
	
	
    /**
	 * Begin step.
	 */
	@Override
	public void beginSequence() {
		super.beginSequence();
		if (fromRange == toRange) {
			targetedActions = fromRange;
		}else {
			// pick a random number within the range
			Random random = new Random();
			targetedActions = toRange - random.nextInt(toRange - fromRange + 1);
		}
		currentAction = 0;
	}
	
    /**
     * Checks whether the step has a next action.
     * @return true if step has a next action otherwise false
     */
	@Override
    protected boolean hasNextAction() {
    	return currentAction < targetedActions;
    }
	
    /**
     * Proceed to next action.
     */
	@Override
    protected void nextAction() {
    	super.nextAction();
    	currentAction++;
    }
    

    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	// keyword
    	result.append("Step:");
    	if (getTitle() != null) {    	
	    	result.append(getTitle());    	
    	}
    	result.append(System.getProperty("line.separator"));
		result.append("Range " + getFromRange() + " "  + getToRange());	
    	result.append(System.getProperty("line.separator"));
    	if (getGivenCondition() != null) {    	
    		result.append("Given ");	
    		result.append(getGivenCondition().toString());
    	}
    	if (getWhenGestures().size() > 0) {
    		result.append("When ");
    	}
   		for (ConditionalGesture conditionalGesture : getWhenGestures()) {
   			result.append(conditionalGesture.toString());
   		}
    	if (getThenCondition() != null) {
    		result.append("Then ");
    		result.append(getThenCondition().toString());
    	}
    	return result.toString();    	
    }	
    
}
