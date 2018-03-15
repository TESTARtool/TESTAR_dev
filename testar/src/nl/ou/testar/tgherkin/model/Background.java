package nl.ou.testar.tgherkin.model;

import java.util.List;

/**
 * Tgherkin Background.
 *
 */
public class Background extends ScenarioDefinition {
    
    /**
     * Background constructor. 
     * @param title given title
     * @param narrative given narrative
     * @param selection given list of conditional gestures
     * @param oracle given widget tree condition
     * @param steps given list of steps
     */
	public Background(String title, String narrative, List<ConditionalGesture> selection, WidgetTreeCondition oracle, List<Step> steps) {
        super(title, narrative, selection, oracle, steps);
    }
	
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	// keyword
    	result.append(getClass().getSimpleName());    	
    	result.append(":");
    	if (getTitle() != null) {    	
	    	result.append(getTitle());    	
    	}
		result.append(System.getProperty("line.separator"));
    	if (getNarrative() != null) {    	
	    	result.append(getNarrative());    	
	    	result.append(System.getProperty("line.separator"));
    	}
    	if (getSelection().size() > 0) {    	
    		result.append("Selection:");
    	}    	
   		for (ConditionalGesture conditionalGesture : getSelection()) {
   			result.append(conditionalGesture.toString());
   		}
    	if (getOracle() != null) {    	
    		result.append("Oracle:");
    		result.append(getOracle().toString());    	
    	}    	
    	for (Step step : getSteps()) {
    		result.append(step.toString());
    	}
    	return result.toString();    	
    }
	
}
