package nl.ou.testar.tgherkin.model;

import java.util.Collections;
import java.util.List;

import org.fruit.Assert;

/**
 * Tgherkin Scenario.
 *
 */
public class Scenario extends ScenarioDefinition {
    
	private final List<Tag> tags;

    /**
     * Scenario constructor. 
     * @param tags given list of tags
     * @param title given title
     * @param narrative given narrative
     * @param selection given list of conditional gestures
     * @param oracle given widget tree condition
     * @param steps given list of steps
     */
    public Scenario(List<Tag> tags, String title, String narrative, List<ConditionalGesture> selection, WidgetTreeCondition oracle,List<Step> steps) {
    	super(title, narrative, selection, oracle, steps);
        Assert.notNull(title);
        Assert.notNull(tags);
        this.tags = Collections.unmodifiableList(tags);
    }

    /**
     * Retrieve tags.
     * @return list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	for (Tag tag : getTags()) {
    		result.append(tag.toString());
    		result.append(System.getProperty("line.separator"));
    	}
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
    	if (getSelection() != null) {    	
    		result.append("Selection:");
    		result.append(getSelection().toString());    	
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
