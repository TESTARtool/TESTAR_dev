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
 * Abstract class that defines a scenario.
 *
 */
public abstract class ScenarioDefinition {
    
    private final String title;
    private final String narrative;
    private final List<ConditionalGesture> selection; 
	private final WidgetTreeCondition oracle;
    private final List<Step> steps;
    private int index;
    
	/**
     * ScenarioDefinition constructor. 
     * @param title given title
     * @param narrative given narrative
     * @param selection given list of conditional gestures
     * @param oracle given widget tree condition
     * @param steps given list of steps
     */
    public ScenarioDefinition(String title, String narrative, List<ConditionalGesture> selection, WidgetTreeCondition oracle, List<Step> steps) {
    	Assert.notNull(selection);
        Assert.notNull(steps);
    	this.title = title;
        this.narrative = narrative;
        this.selection = Collections.unmodifiableList(selection);
        this.oracle = oracle;
        this.steps = Collections.unmodifiableList(steps);
        index = -1;        		
    }

    /**
     * Retrieve title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieve narrative.
     * @return narrative
     */
    public String getNarrative() {
        return narrative;
    }

	/**
	 * Retrieve selection.
	 * @return list of conditional gestures
	 */
    public List<ConditionalGesture> getSelection() {
		return selection;
	}

	/**
	 * Retrieve oracle.
	 * @return widget tree condition
	 */
	public WidgetTreeCondition getOracle() {
		return oracle;
	}
    
    /**
     * Retrieve steps.
     * @return list of steps
     */
    public List<Step> getSteps() {
        return steps;
    }
    
    /**
     * Retrieve step index.
     * @return step index
     */
    protected int getIndex() {
		return index;
	}

	/**
	 * Set step index.
	 * @param index step index
	 */
	protected void setIndex(int index) {
		this.index = index;
	}
   
    /**
	 * Check whether more actions exist and proceed to next action if more actions exist.
	 * @param proxy document protocol proxy
	 * @return true if more actions exist, otherwise false
	 */
	public boolean moreActions(ProtocolProxy proxy) {
		if (currentStep() != null && currentStep().hasNextAction(proxy, null)) {
			return true;
		} else {
			// search for next step with actions
			int savedIndex = index;
			while (hasNextStep()) {
				nextStep();
				if (currentStep().hasNextAction(proxy, null)) {
					index = savedIndex;
					return true;
				}
			}
			index = savedIndex;
			return false;			
		}
	}
	
	/**
	 * Check whether more sequences exist.
	 * @return true if more sequences exist, otherwise false
	 */
	public boolean moreSequences() {
		return false;
	}

	/**
	 * Begin sequence.
	 */
	public void beginSequence() {
		for (Step step : getSteps()) {
			//  Randomize in advance, so moreActions can be determined: StepRange number of actions could be zero actions
			step.beginSequence();
		}
		index = -1;
	}
    
	/**	  
	 * Evaluate given condition.
	 * @param proxy document protocol proxy
	 * @return true if given condition is applicable, otherwise false 
	 */
	public boolean evaluateGivenCondition(ProtocolProxy proxy) {
		if (currentStep() != null && currentStep().hasNextAction(proxy, null)) {
			// current step has more actions
			currentStep().nextAction();
		} else {
			// search for next step with actions
			while (hasNextStep()) {
				nextStep();
				if (currentStep().hasNextAction(proxy, null)) {
					currentStep().nextAction();
					break;
				}
			}
		}
		return currentStep().evaluateGivenCondition(proxy, null, mismatchOccurred());
	}

	/**	  
	 * Evaluate when condition.
	 * @param proxy document protocol proxy
	 * @param map map with widget as key and list of gestures as value
	 * @return set of actions
	 */
	public Set<Action> evaluateWhenCondition(ProtocolProxy proxy, Map<Widget,List<Gesture>> map) {
		// apply scenario level selection
		Step.evaluateWhenCondition(proxy, map, selection, null);
		// apply step level selection		
		return currentStep().evaluateWhenCondition(proxy, map, null, mismatchOccurred());
	}
	

	/**	  
	 * Get verdict.
	 * @param proxy document protocol proxy
	 * @return oracle verdict, which determines whether the state is erroneous and why  
	 */
	public Verdict getVerdict(ProtocolProxy proxy) {
		// scenario level
		if (oracle != null && !oracle.evaluate(proxy, null)) {
			setFailed();
			Report.appendReportDetail(Report.BooleanColumn.THEN,false);
			return new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin scenario oracle failure!");
		}
		// step level
		return currentStep().getVerdict(proxy, null, mismatchOccurred());
	}

	/**
     * Retrieve whether current action resulted in a failure.
     * @return true if current action failed otherwise false 
     */
	public boolean hasFailed() {
		if (currentStep() != null) {
			return currentStep().getStatus() == Step.Status.FAILED;
		}
		return false;
	}
	
	
	/**
     * Reset scenario definition.
     */
	public void reset() {
		index = -1;
		for (Step step : getSteps()) {
			step.reset();
		}
	}

	/**
     * Check.
     * @return list of error descriptions, empty list if no errors exist
     */
	public List<String> check() {
		List<String> list = new ArrayList<String>();
   		for (ConditionalGesture conditionalGesture : getSelection()) {
			list.addAll(conditionalGesture.check(null));
   		}
		if (getOracle() != null) {
			list.addAll(getOracle().check(null));
		}
		for (Step step : getSteps()) {
			list.addAll(step.check(null));
		}
		return list;
	}

	/**
     * Retrieve whether a mismatch occurred.
     * @return true if mismatch occurred, otherwise false
     */
	public boolean mismatchOccurred() {
		// search steps until current index 
		for (int idx = 0; idx <= index; idx++) {
			if (steps.get(idx).isMismatch()) {
				return true;
			}
		}
		return false;
	}
	
	/**
     * Set current step to failed.
     */
	protected void setFailed() {
		if (currentStep() != null) {
			currentStep().setStatus(Step.Status.FAILED);	
		}
	}
	
    /**
     * Checks whether the scenario definition has a next step.
     * @return true if scenario definition has a next step otherwise false
     */
    protected boolean hasNextStep() {
        return index + 1 < steps.size();
    }

    /**
     * Retrieve next step.
     * @return next step
     */
    protected Step nextStep() {
    	index++; 
        return steps.get(index);
    }
	
    /**
     * Retrieve current step.
     * @return current step, returns null if no current step exists
     */
    protected Step currentStep() {
    	if (index < 0 || index >= steps.size()) {
    		return null;
    	}
    	return steps.get(index);
    }   
    
}
