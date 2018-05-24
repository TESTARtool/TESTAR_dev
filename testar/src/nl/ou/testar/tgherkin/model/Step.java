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
import org.fruit.alayer.actions.NOP;
import org.fruit.monkey.ConfigTags;

import nl.ou.testar.tgherkin.protocol.DerivedGesturesReportItem;
import nl.ou.testar.tgherkin.protocol.Report;
import nl.ou.testar.utils.report.Reporter;

/**
 * Representation of a Tgherkin step.
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
	private boolean retryMode;
	private int nrOfRetries; 
    

    /**
     * Step constructor.
     * @param title summary description
     * @param givenCondition widget tree condition that defines the Given clause
     * @param whenGestures list of conditional gestures that defines the When clause 
     * @param thenCondition widget tree condition that defines the Then clause
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
	 * @param mismatch to be set mismatch value
	 */
	public void setMismatch(boolean mismatch) {
		this.mismatch = mismatch;
	}
	
	/**
	 * Retrieve retry mode.
	 * @return true if in retry mode, otherwise false
	 */
	public boolean isRetryMode() {
		return retryMode;
	}

	/**
	 * Set retry mode.
	 * @param retryMode to be set retry mode
	 */
	protected void setRetryMode(boolean retryMode) {
		this.retryMode = retryMode;
	}

	/**
	 * Retrieve number of NOP action retries.
	 * @return number of NOP action retries
	 */
	public int getNrOfRetries() {
		return nrOfRetries;
	}


	/**
	 * Set number of NOP action retries.
	 * @param nrOfRetries to be set number of NOP action retries
	 */
	protected void setNrOfRetries(int nrOfRetries) {
		this.nrOfRetries = nrOfRetries;
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
    	nrOfRetries = 0;
    	retryMode = false;
	}
    
    
	/**	  
	 * Evaluate given condition.
	 * @param proxy document protocol proxy
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @param mismatchOccurred indicator whether a mismatch occurred during execution of the current scenario
	 * @return  true if given condition is applicable, otherwise false 
	 */
	public boolean evaluateGivenCondition(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		// Step
		Report.appendReportDetail(Report.StringColumn.STEP,getTitle());
		boolean result = true;
		if (!mismatchOccurred || !proxy.getSettings().get(ConfigTags.ContinueToApplyDefault)) {			
			if (givenCondition != null) {
				result = givenCondition.evaluate(proxy, dataTable);
				if (!result ) {
					if (nrOfRetries >= proxy.getSettings().get(ConfigTags.TgherkinNrOfNOPRetries)) {
						setMismatch(true);
						if (!proxy.getSettings().get(ConfigTags.ApplyDefaultOnMismatch)) {
							setStatus(Status.FAILED);
							result = false;
						}
					}else {
						retryMode = true;
					}
				}	
			}
			Report.appendReportDetail(Report.BooleanColumn.GIVEN_MISMATCH, isMismatch());
		}
		Report.appendReportDetail(Report.BooleanColumn.GIVEN,result);
		return result;
	}
    
	/**	  
	 * Evaluate when condition.
	 * @param proxy document protocol proxy
	 * @param map map with widget as key and list of gestures as value
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @param mismatchOccurred indicator whether a mismatch occurred during execution of the current scenario
	 * @return derived set of actions
	 */
	public Set<Action> evaluateWhenCondition(ProtocolProxy proxy, Map<Widget,List<Gesture>> map, DataTable dataTable, boolean mismatchOccurred) {
		Set<Action> actions = new HashSet<Action>();
		if (!retryMode && (!mismatchOccurred || !proxy.getSettings().get(ConfigTags.ContinueToApplyDefault))) {			
			Map<Widget,List<Gesture>> oldMap = copy(map);
			evaluateWhenCondition(proxy, map, whenGestures, dataTable);
			if (map.size() == 0) {
				if (nrOfRetries >= proxy.getSettings().get(ConfigTags.TgherkinNrOfNOPRetries)) {
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
					retryMode = true;
					Report.appendReportDetail(Report.BooleanColumn.WHEN_MISMATCH,false);
				}
			}else {
				Report.appendReportDetail(Report.BooleanColumn.WHEN_MISMATCH,false);
			}
		}
		if (!retryMode && proxy.getSettings().get(ConfigTags.ReportDerivedGestures)){
			Reporter.getInstance().report(new DerivedGesturesReportItem(false, proxy.getSequenceCount(), proxy.getActionCount(), map));
		}
		// generate actions
		if (retryMode) {
			actions.add(new NOP());
		}else {
			Iterator<Map.Entry<Widget,List<Gesture>>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Widget,List<Gesture>> entrySet = iterator.next();
				Widget widget = entrySet.getKey();
				List<Gesture> list = entrySet.getValue();
				for(Gesture gesture : list) {
					actions.addAll(gesture.getActions(widget, proxy, dataTable));
				}
				// store widget 
				proxy.storeWidget(proxy.getState().get(Tags.ConcreteID), widget);
			}
		}
		Report.appendReportDetail(Report.IntegerColumn.WHEN_DERIVED_ACTIONS,actions.size());
		return actions;
	}
	
	/**
	 * Evaluate when condition.
	 * @param proxy document protocol proxy
	 * @param map map with widget as key and list of gestures as value
	 * @param select filter defined by a list of conditional gestures
	 * @param table data table contained in the examples section of a scenario outline
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
		if (originalList.contains(new AnyGesture(new ParameterBase()))) {
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
	 * @param proxy document protocol proxy
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @param mismatchOccurred indicator whether a mismatch occurred during execution of the current scenario
	 * @return oracle verdict, which determines whether the state is erroneous and why 
	 */
	public Verdict getVerdict(ProtocolProxy proxy, DataTable dataTable, boolean mismatchOccurred) {
		if (retryMode) {
			// NOP action has been executed: do not use step oracle to get verdict. 
			Report.appendReportDetail(Report.BooleanColumn.THEN,true);
			Report.appendReportDetail(Report.BooleanColumn.THEN_MISMATCH,false);
			return new Verdict(Verdict.SEVERITY_MIN, "Tgherkin NOP action in retry mode");
		}	
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
     * @param dataTable data table contained in the examples section of a scenario outline
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
     * @return true if step has a next action, otherwise false
     */
    protected boolean hasNextAction() {
    	return !running || retryMode;
    }
	
    /**
     * Proceed to next action.
     */
    protected void nextAction() {
    	running = true;
    	if (retryMode) {
    		nrOfRetries++;
    		retryMode = false;
    	}
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