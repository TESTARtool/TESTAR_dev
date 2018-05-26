package nl.ou.testar.tgherkin.model;

import java.util.List;

/**
 * Step that repeats itself.
 */
public abstract class RepeatingStep extends Step {

	private boolean generalMismatch;
	private int currentAction;
	
	/**
     * RepeatingStep Constructor.
     * @param title summary description
     * @param givenCondition widget tree condition that defines the Given clause
     * @param whenGestures list of conditional gestures that defines the When clause 
     * @param thenCondition widget tree condition that defines the Then clause
     */
    public RepeatingStep(String title, WidgetTreeCondition givenCondition, List<ConditionalGesture> whenGestures, WidgetTreeCondition thenCondition) {
        super(title, givenCondition, whenGestures, thenCondition);
    }

	/**
	 * Get current action number.
	 * @return current action number
	 */
	protected int getCurrentAction() {
		return currentAction;
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
	public void beginSequence() {
		super.beginSequence();
		currentAction = 0;
	}
	
	
	@Override
    protected void nextAction() {
		if (!isRetryMode()){
	    	currentAction++;
	    	setNrOfRetries(0);
		}
		super.nextAction();
    }

}
