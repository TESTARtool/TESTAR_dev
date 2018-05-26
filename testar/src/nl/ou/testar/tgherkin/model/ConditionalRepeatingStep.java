package nl.ou.testar.tgherkin.model;

import java.util.List;

/**
 * Step that conditionally repeats itself.
 */
public class ConditionalRepeatingStep extends RepeatingStep {

	/**
	 * Type.
	 *
	 */
	public enum Type {
	    /**
	     * While type: test before and loop while condition is true. 
	     */
	    WHILE_STEP,
	    /**
	     * Repeat until type: test after and loop until condition is true.
	     */
	    REPEAT_UNTIL_STEP
	  }

	private final WidgetTreeCondition loopCondition;
	private final Type type;
	
	/**
     * ConditionalRepeatingStep constructor.
     * @param title summary description
     * @param type type of loop
     * @param loopCondition condition that defines repetition of the step
     * @param givenCondition widget tree condition that defines the Given clause
     * @param whenGestures list of conditional gestures that defines the When clause 
     * @param thenCondition widget tree condition that defines the Then clause
     */
    public ConditionalRepeatingStep(String title, Type type, WidgetTreeCondition loopCondition, WidgetTreeCondition givenCondition, List<ConditionalGesture> whenGestures, WidgetTreeCondition thenCondition) {
        super(title, givenCondition, whenGestures, thenCondition);
        this.type = type;
        this.loopCondition = loopCondition;
    }

	/**
	 * Retrieve loop type.
	 * @return loop type
	 */
	public Type getType() {
		return type;
	}

	/**
     * Retrieve loop condition.
     * @return loop condition
     */
    public WidgetTreeCondition getLoopCondition() {
		return loopCondition;
	}

	@Override
	public List<String> check(DataTable dataTable) {
		List<String> list = super.check(dataTable);
		if (getLoopCondition() != null) {
			list.addAll(getLoopCondition().check(dataTable));
		}
		return list;
	}
	
	@Override
    protected boolean hasNextAction(ProtocolProxy proxy, DataTable dataTable) {
		if (getCurrentAction() == 0 && getType() == Type.REPEAT_UNTIL_STEP) {
			return true;
		}
		if (getType() == Type.WHILE_STEP) {
			return getLoopCondition().evaluate(proxy, dataTable) || isRetryMode();
		}
		// repeat until loop
    	return !getLoopCondition().evaluate(proxy, dataTable) || isRetryMode();
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
    	if (getType() == Type.WHILE_STEP) {
    		result.append("While " + result.append(getLoopCondition().toString()));	
    	}else{
    		result.append("Repeat until " + result.append(getLoopCondition().toString()));
    	}
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
