package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Widget;

/**
 * Class that defines and evaluates a widget tree condition.
 *
 */
public class WidgetTreeCondition {
    
	private final List<WidgetCondition> widgetConditions;

    /**
     * WidgetTreeCondition constructor.
     * @param widgetConditions list of widget conditions
     */
    public WidgetTreeCondition(List<WidgetCondition> widgetConditions) {
    	Assert.notNull(widgetConditions);
    	this.widgetConditions = Collections.unmodifiableList(widgetConditions);
    }

	/**
     * Retrieve widget conditions.
     * @return list of widget conditions
     */
    public List<WidgetCondition> getWidgetConditions() {
        return widgetConditions;
    }
    
	/**
	 * Evaluate widget tree condition.
	 * @param proxy document protocol proxy
	 * @param dataTable data table contained in the examples section of a scenario outline
	 * @return  true if condition is applicable for the given state, otherwise false 
	 */
	public boolean evaluate(ProtocolProxy proxy, DataTable dataTable) {
		// true if one or more widgets are found for which the tree condition is applicable
		// This means individual widget conditions can evaluate to true for different widgets.
		// Also (and operator) has higher priority then Either (or operator)
		boolean result = false;
		for (WidgetCondition widgetCondition : widgetConditions) {
			if (widgetCondition.getType() == null) {
				// first condition
				result = evaluateCondition(proxy, dataTable, widgetCondition);
			}else {
				switch(widgetCondition.getType()) {
				case ALSO:
					if (result) {
						// only evaluate if intermediary result is still true
						result = evaluateCondition(proxy, dataTable, widgetCondition);					
					}
					break;
				case EITHER:
					if (result) {
						// previous intermediary result evaluated to true  
						return true;					
					}
					result = evaluateCondition(proxy, dataTable, widgetCondition);
				default:
				}
			}
		}
		return result;
	}

	private boolean evaluateCondition(ProtocolProxy proxy, DataTable dataTable, WidgetCondition widgetCondition) {
		// true if a widget is found for which the condition is applicable
		for (Widget widget : proxy.getState()) {
			if (widgetCondition.evaluate(proxy, widget, dataTable)) {
				return true;
			}
		}
		return false;
	}
	
	/**
     * Check.
     * @param dataTable data table contained in the examples section of a scenario outline
     * @return list of error descriptions, empty list if no errors exist
     */
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		for (WidgetCondition widgetCondition : widgetConditions) {
			list.addAll(widgetCondition.check(dataTable));
		}	
		return list;
	}
	
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	if (getWidgetConditions() != null) {
    		for (WidgetCondition widgetCondition : getWidgetConditions()) {
	    		result.append(widgetCondition.toString());
	    		result.append(System.getProperty("line.separator"));
	    	}
    	}
    	return result.toString();    	
    }
    
}
