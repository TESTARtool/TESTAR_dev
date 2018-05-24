package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.protocol.Report;

/**
 * Representation of a Tgherkin scenario outline.
 *
 */
public class ScenarioOutline extends ScenarioDefinition {
    private final List<Tag> tags;
    private final Examples examples;

    /**
     * ScenarioOutline constructor. 
     * @param tags list of tags
     * @param title summary description
     * @param narrative detailed description
     * @param selection list of conditional gestures that defines a filter on the set of derivable gestures
     * @param oracle widget tree condition that serves as an oracle verdict
     * @param steps list of consecutive steps
     * @param examples container for a data table
     */
    public ScenarioOutline(List<Tag> tags, String title, String narrative, List<ConditionalGesture> selection, WidgetTreeCondition oracle,List<Step> steps, Examples examples) {
        super(title, narrative, selection, oracle, steps);
        Assert.notNull(title);
        Assert.notNull(tags);
        Assert.notNull(examples);
        this.tags = Collections.unmodifiableList(tags);
        this.examples = examples;
    }

    /**
     * Retrieve tags.
     * @return list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Retrieve examples.
     * @return examples
     */
    public Examples getExamples() {
        return examples;
    }
    
    @Override
	public boolean moreSequences() {
		return getExamples().moreSequences();
	}
    
    @Override
	public void beginSequence() {
		super.beginSequence();
		super.reset();
		getExamples().beginSequence();
	}
	
    @Override
	public boolean evaluateGivenCondition(ProtocolProxy proxy) {
		if (currentStep() != null && currentStep().hasNextAction()) {
			// current step has more actions
			currentStep().nextAction();
		}else {
			// search for next step with actions
			while (hasNextStep()) {
				nextStep();
				if (currentStep().hasNextAction()) {
					currentStep().nextAction();
					break;
				}
			}
		}
		return currentStep().evaluateGivenCondition(proxy, examples.getDataTable(), mismatchOccurred());
	}
	
    @Override
	public Set<Action> evaluateWhenCondition(ProtocolProxy proxy, Map<Widget,List<Gesture>> map) {
		// apply scenario level selection
		Step.evaluateWhenCondition(proxy, map, getSelection(), examples.getDataTable());
		// apply step level selection
		return currentStep().evaluateWhenCondition(proxy, map, examples.getDataTable(), mismatchOccurred());
	}
	
    @Override
	public Verdict getVerdict(ProtocolProxy proxy) {
		// scenario level
		if (getOracle() != null && !getOracle().evaluate(proxy, examples.getDataTable())){
			setFailed();
			Report.appendReportDetail(Report.BooleanColumn.THEN,false);
			return new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin scenario outline oracle failure!");
		}
		// step level
		return currentStep().getVerdict(proxy, examples.getDataTable(), mismatchOccurred());
	}

    @Override
	public void reset() {
		super.reset();
		getExamples().reset();
	}
	
    @Override
	public List<String> check() {
		List<String> list = new ArrayList<String>();
   		for (ConditionalGesture conditionalGesture : getSelection()) {
			list.addAll(conditionalGesture.check(getExamples().getDataTable()));
   		}
		if (getOracle() != null) {
			list.addAll(getOracle().check(getExamples().getDataTable()));
		}
		for (Step step : getSteps()) {
			list.addAll(step.check(getExamples().getDataTable()));
		}
		return list;
	}

    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	for (Tag tag : getTags()) {
    		result.append(tag.toString());
    		result.append(System.getProperty("line.separator"));
    	}
    	// keyword
    	result.append("Scenario Outline:");
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
    	if (getExamples() != null) {    	
	    	result.append(getExamples().toString());    	
    	}
    	return result.toString();    	
    }
    
}
