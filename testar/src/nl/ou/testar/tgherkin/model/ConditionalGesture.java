package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.monkey.Settings;

/**
 * Tgherkin ConditonalGesture.
 *
 */
public class ConditionalGesture {
    
	private final WidgetCondition widgetCondition;
	private final Gesture gesture;

    /**
     * ConditionalGesture constructor.
     * @param widgetCondition given widget condition
     * @param gesture given gesture
     */
    public ConditionalGesture(WidgetCondition widgetCondition, Gesture gesture) {
        Assert.notNull(gesture);
    	this.widgetCondition = widgetCondition;
    	this.gesture = gesture;
    }
    
	/**
     * Retrieve widget condition.
     * @return widget condition
     */
    public WidgetCondition getWidgetCondition() {
        return widgetCondition;
    }
    
	/**
	 * Retrieve gesture.
	 * @return gesture
	 */
	public Gesture getGesture() {
		return gesture;
	}
	
	/**
	 * Check whether widget is a candidate for the conditional gesture.
	 * @param settings given settings
	 * @param proxy given action widget proxy
	 * @param state given state
	 * @param widget given widget
	 * @param dataTable given data table
	 * @return true if widget is a candidate, otherwise false
	 */
	public boolean isCandidate(Settings settings, ActionWidgetProxy proxy, State state, Widget widget, DataTable dataTable) {
		return getGesture().gesturePossible(widget, proxy, dataTable) && (getWidgetCondition() == null || getWidgetCondition().evaluate(settings, state, widget, dataTable));
	}

	/**	  
	 * Evaluate when condition.
	 * @param settings given settings
	 * @param state the SUT's current state
	 * @param proxy given action widget proxy
	 * @param widget given widget
	 * @param dataTable given data table
	 * @return  set of derived actions 
	 */
	public Set<Action> evaluateWhenCondition(Settings settings, State state, ActionWidgetProxy proxy, Widget widget, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();
		if (isCandidate(settings, proxy, state, widget, dataTable)) {
			// widget is target for the gesture: return corresponding action 
			actions.addAll(getGesture().getActions(widget, proxy, dataTable));	
		}
		return actions;
	}
	
	/**
     * Check conditional gesture.
     * @param dataTable given data table
     * @return list of error descriptions
     */
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		if (getWidgetCondition() != null) {
			list.addAll(getWidgetCondition().check(dataTable));
		}
		list.addAll(getGesture().check(dataTable));
		return list;
	}	

    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	if (getWidgetCondition() != null) {
    		result.append(getWidgetCondition().toString());
    		result.append(" ");
    	}
    	if (getGesture() != null) {
    		result.append(getGesture().toString());
    	}
    	return result.toString();    	
    }

}