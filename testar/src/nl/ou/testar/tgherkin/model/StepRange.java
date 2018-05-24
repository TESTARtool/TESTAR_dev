package nl.ou.testar.tgherkin.model;

import java.util.List;
import java.util.Random;

/**
 * Step that iterates a fixed or random number of times.
 */
public class StepRange extends Step {

	private final int fromRange;
	private final int toRange;
	private int targetedActions;
	private int currentAction;
	private boolean generalMismatch;
	
	/**
     * StepRange constructor.
     * @param title summary description
     * @param fromRange from range that defines the minimum number of iterations
     * @param toRange to range that defines the maximum number of iterations
     * @param givenCondition widget tree condition that defines the Given clause
     * @param whenGestures list of conditional gestures that defines the When clause 
     * @param thenCondition widget tree condition that defines the Then clause
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
	

	@Override
	public boolean isMismatch() {
		return generalMismatch;
	}

	@Override
	public void setMismatch(boolean mismatch) {
		if (mismatch) {
			generalMismatch = mismatch;
		}
		super.setMismatch(mismatch);
	}
	
	@Override
    public void reset() {
    	super.reset();
    	generalMismatch = false;
	}
	

	@Override
	public boolean evaluateGivenCondition(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		// reset status
		setStatus(Status.UNDETERMINED);
		setMismatch(false);
		return super.evaluateGivenCondition(proxy, dataTable, mismatchOccurred);
	}
	
	
	@Override
	public List<String> check(DataTable dataTable) {
		List<String> list = super.check(dataTable);
		if (fromRange > toRange) {
			list.add("Validation error - invalid range for step " + getTitle() + ": from range " + fromRange + " greater then To range " + toRange + "\n");
		}
		return list;
	}
	
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
	
	@Override
    protected boolean hasNextAction() {
    	return currentAction < targetedActions || isRetryMode();
    }
	
	@Override
    protected void nextAction() {
		if (!isRetryMode()){
	    	currentAction++;
	    	setNrOfRetries(0);
		}
		super.nextAction();
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
