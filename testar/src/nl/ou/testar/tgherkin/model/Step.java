package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;

import nl.ou.testar.tgherkin.protocol.Report;

/**
 * Tgherkin Step.
 *
 */
public class Step {

	/**
	 * Tgherkin failure value.
	 */
	public static final double TGHERKIN_FAILURE = 0.00000009;

	/**
	 * Status.
	 *
	 */
	public enum Status {
		/**
		 * Status undetermined. 
		 */
		UNDETERMINED,
		/**
		 * Status passed.
		 */
		PASSED,
		/**
		 * Status failed.
		 */
		FAILED
	}

    private final String title;
    private final WidgetTreeCondition givenCondition;
    private final List<ConditionalGesture> whenGestures;
    private final WidgetTreeCondition thenCondition;
    private boolean running;
	private Status status;
	private boolean mismatch;
    

    /**
     * Step constructor.
     * @param title title
     * @param givenCondition given widget tree condition
     * @param whenGestures list of conditional gestures
     * @param thenCondition then widget tree condition
     */
    public Step(String title, WidgetTreeCondition givenCondition, List<ConditionalGesture> whenGestures, WidgetTreeCondition thenCondition) {
    	Assert.notNull(title);
    	Assert.notNull(whenGestures);
    	this.title = title;
    	this.givenCondition = givenCondition;
    	this.whenGestures = Collections.unmodifiableList(whenGestures);
    	this.thenCondition = thenCondition;
    }

    /**
     * Retrieve title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
	 * Retrieve given condition. 
	 * @return given condition
	 */
	public WidgetTreeCondition getGivenCondition() {
		return givenCondition;
	}


	/**
	 * Retrieve when gestures. 
	 * @return list of when gestures
	 */
	public List<ConditionalGesture> getWhenGestures() {
		return whenGestures;
	}


	/**
	 * Retrieve then condition. 
	 * @return then widget tree condition
	 */
	public WidgetTreeCondition getThenCondition() {
		return thenCondition;
	}
	
	
	/**
	 * Retrieve status.
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set status.
	 * @param status given status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Retrieve mismatch.
	 * @return true if mismatch, otherwise false
	 */
	public boolean isMismatch() {
		return mismatch;
	}

	/**
	 * Set mismatch.
	 * @param mismatch given mismatch
	 */
	public void setMismatch(boolean mismatch) {
		this.mismatch = mismatch;
	}
	
	/**
	 * Begin step.
	 */
	public void beginSequence() {
		reset();
	}

	/**
	 * Reset step.
	 */
    public void reset() {
    	running = false;
    	status = Status.UNDETERMINED;
    	mismatch = false;
	}
    
    
	/**	  
	 * Evaluate given condition.
	 * @param proxy given protocol proxy
	 * @param dataTable given data table
	 * @param mismatchOccurred indicator whether a mismatch occurred
	 * @return  true if given condition is applicable, otherwise false 
	 */
	public boolean evaluateGivenCondition(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		// Step
		Report.appendReportDetail(Report.StringColumn.STEP,getTitle());
		boolean result = true;
		if (!mismatchOccurred || !proxy.getSettings().get(ConfigTags.ContinueToApplyDefault)) {			
			if (givenCondition != null) {
				result = givenCondition.evaluate(proxy, dataTable);
				if (result) {
					Report.appendReportDetail(Report.BooleanColumn.GIVEN_MISMATCH,false);
				}else {
					setMismatch(true);
					Report.appendReportDetail(Report.BooleanColumn.GIVEN_MISMATCH,true);
					if (!proxy.getSettings().get(ConfigTags.ApplyDefaultOnMismatch)) {
						setStatus(Status.FAILED);
					}else {
						result = true;
					}
				}
			}
		}
		Report.appendReportDetail(Report.BooleanColumn.GIVEN,result);
		return result;
	}
    
	/**	  
	 * Evaluate when condition.
	 * @param proxy given protocol proxy
	 * @param map widget-list of gestures map
	 * @param table data table
	 * @param mismatchOccurred indicator whether a mismatch occurred
	 * @return set of actions
	 */
	public Set<Action> evaluateWhenCondition(ProtocolProxy proxy, Map<Widget,List<Gesture>> map, DataTable table, boolean mismatchOccurred) {
		Set<Action> actions = new HashSet<Action>();
		if (!mismatchOccurred || !proxy.getSettings().get(ConfigTags.ContinueToApplyDefault)) {			
			Map<Widget,List<Gesture>> oldMap = copy(map);
			evaluateWhenCondition(proxy, map, whenGestures, table);
			if (map.size() == 0) {
				// current step level execution resulted in mismatch
				setMismatch(true);
				Report.appendReportDetail(Report.BooleanColumn.WHEN_MISMATCH,true);
				if (proxy.getSettings().get(ConfigTags.ApplyDefaultOnMismatch)) {
					// restore map if default should be applied 
					map = oldMap;
				}else {
					setStatus(Status.FAILED);
				}
			}else {
				Report.appendReportDetail(Report.BooleanColumn.WHEN_MISMATCH,false);
			}
		}
		if (proxy.getSettings().get(ConfigTags.ReportDerivedGestures)){
			Report.reportDerivedGestures(proxy, map);		
		}
		// generate actions
		Iterator<Map.Entry<Widget,List<Gesture>>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Widget,List<Gesture>> entrySet = iterator.next();
			Widget widget = entrySet.getKey();
			List<Gesture> list = entrySet.getValue();
			for(Gesture gesture : list) {
				actions.addAll(gesture.getActions(widget, proxy, table));
			}
			// store widget 
			proxy.storeWidget(proxy.getState().get(Tags.ConcreteID), widget);
		}
		Report.appendReportDetail(Report.IntegerColumn.WHEN_DERIVED_ACTIONS,actions.size());
		return actions;
	}
	
	/**
	 * Evaluate when condition.
	 * @param proxy given protocol proxy
	 * @param map widget-list of gestures map
	 * @param select given list of conditional gestures
	 * @param table given data table
	 */
	protected static void evaluateWhenCondition(ProtocolProxy proxy, Map<Widget,List<Gesture>> map, List<ConditionalGesture> select, DataTable table) {
		if (select.size() > 0) {
			Iterator<Map.Entry<Widget,List<Gesture>>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Widget,List<Gesture>> entrySet = iterator.next();
				Widget widget = entrySet.getKey();
				List<Gesture> originalList = entrySet.getValue();
				List<Gesture> newList = new ArrayList<Gesture>();
				for(ConditionalGesture conditionalGesture : select) {
					if (conditionalGesture.isCandidate(proxy, widget, table)) {
						newList.addAll(getFilteredGestures(originalList, conditionalGesture.getGesture()));
					}
				}
				if (newList.size() > 0) {
					map.put(widget, newList);	
				}else {
					iterator.remove();
				}
			}
		}
	}

	private static List<Gesture> getFilteredGestures(List<Gesture> originalList, Gesture gesture){
		List<Gesture> resultList = new ArrayList<Gesture>();
		if (originalList.contains(gesture)) {
			resultList.add(gesture);
			return resultList;
		}
		if (gesture instanceof AnyGesture) {
			for(Gesture originalGesture : originalList) {
				resultList.add(originalGesture);
			}
			return resultList;
		}
		if (originalList.contains(new AnyGesture(new ArrayList<Argument>()))) {
			resultList.add(gesture);
		}
		return resultList;
	}	

	private static Map<Widget,List<Gesture>> copy(Map<Widget,List<Gesture>> original){
		Map<Widget,List<Gesture>> copy = new HashMap<Widget,List<Gesture>>();
		for (Map.Entry<Widget,List<Gesture>> entry : original.entrySet()){
			copy.put(entry.getKey(),new ArrayList<Gesture>(entry.getValue()));
	    }
		return copy;
	}	
	

	/**	  
	 * Get verdict.
	 * @param proxy given protocol proxy
	 * @param dataTable given data table
	 * @param mismatchOccurred indicator whether a mismatch occurred
	 * @return oracle verdict, which determines whether the state is erroneous and why 
	 */
	public Verdict getVerdict(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		if (!mismatchOccurred || !proxy.getSettings().get(ConfigTags.ContinueToApplyDefault)) {			
			if (thenCondition != null && !thenCondition.evaluate(proxy, dataTable)) { 
				setMismatch(true);
				Report.appendReportDetail(Report.BooleanColumn.THEN_MISMATCH,true);
				if (!proxy.getSettings().get(ConfigTags.ApplyDefaultOnMismatch)) {
					setStatus(Status.FAILED);
					Report.appendReportDetail(Report.BooleanColumn.THEN,false);					
					return new Verdict(TGHERKIN_FAILURE, "Tgherkin step oracle failure!");
				}
			}else {
				Report.appendReportDetail(Report.BooleanColumn.THEN_MISMATCH,false);
			}
		}
		Report.appendReportDetail(Report.BooleanColumn.THEN,true);
		if (getStatus() == Status.FAILED) {
			return new Verdict(TGHERKIN_FAILURE, "Tgherkin step failure!");
		}else {
			setStatus(Status.PASSED);
			if (isMismatch()) {
				return new Verdict(Verdict.SEVERITY_MIN, "Default applied for Tgherkin step mismatch");
			}
			if (mismatchOccurred && proxy.getSettings().get(ConfigTags.ContinueToApplyDefault)) {
				return new Verdict(Verdict.SEVERITY_MIN, "Default applied after a Tgherkin step mismatch");
			}
			return Verdict.OK;
		}
	}

	/**
     * Check.
     * @param dataTable given data table
     * @return list of error descriptions
     */
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		if (getGivenCondition() != null) {
			list.addAll(getGivenCondition().check(dataTable));
		}
   		for (ConditionalGesture conditionalGesture : getWhenGestures()) {
			list.addAll(conditionalGesture.check(dataTable));
   		}
		if (getThenCondition() != null) {
			list.addAll(getThenCondition().check(dataTable));
		}
		return list;
	}

    /**
     * Checks whether the step has a next action.
     * @return true if step has a next action otherwise false
     */
    protected boolean hasNextAction() {
    	return !running;
    }
	
    /**
     * Proceed to next action.
     */
    protected void nextAction() {
    	running = true;
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