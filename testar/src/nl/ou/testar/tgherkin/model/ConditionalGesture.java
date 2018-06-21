package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

/**
 * Representation of a conditional gesture that defines the conditions under which a certain gesture is derived.
 *
 */
public class ConditionalGesture {
    
	private final WidgetCondition widgetCondition;
	private final Gesture gesture;

    /**
     * ConditionalGesture constructor.
     * @param widgetCondition condition that indicates when a gesture applies to a widget
     * @param gesture gesture that is applied
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
	 * @param proxy document protocol proxy
	 * @param widget to be evaluated widget 
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @return true if widget is a candidate, otherwise false
	 */
	public boolean isCandidate(ProtocolProxy proxy, Widget widget, DataTable dataTable) {
		return getGesture().gesturePossible(widget, proxy, dataTable) && (getWidgetCondition() == null || getWidgetCondition().evaluate(proxy, widget, dataTable));
	}

	/**	  
	 * Evaluate when condition.
	 * @param proxy document protocol proxy
	 * @param widget to be evaluated widget
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @return set of derived actions for the widget involved 
	 */
	public Set<Action> evaluateWhenCondition(ProtocolProxy proxy, Widget widget, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();
		if (isCandidate(proxy, widget, dataTable)) {
			// widget is target for the gesture: return corresponding action 
			actions.addAll(getGesture().getActions(widget, proxy, dataTable));	
		}
		return actions;
	}
	
	/**
     * Check conditional gesture.
     * @param dataTable data table contained in the examples section of a scenario outline
     * @return list of error descriptions, empty list of no errors exist
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