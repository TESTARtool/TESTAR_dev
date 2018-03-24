package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;


/**
 * Tgherkin Gesture.
 *
 */
public abstract class Gesture {
    
	/**
	 * Scroll arrow size. 
	 */
	public static final double SCROLL_ARROW_SIZE = 36; 
	
	/**
	 * Scroll thickness. 
	 */
	public static final double SCROLL_THICKNESS = 16; 
	
	private final List<Argument> arguments;	

    /**
     * Gesture constructor.
     * @param arguments list of arguments
     */
    public Gesture(List<Argument> arguments) {
    	Assert.notNull(arguments);
    	this.arguments = Collections.unmodifiableList(arguments);
    }

	/**
     * Retrieve arguments.
     * @return list of arguments
     */
    public List<Argument> getArguments() {
        return arguments;
    }
    
	/**
	 * Retrieve whether gesture is possible on a given widget.
	 * @param widget given widget
	 * @param proxy given action widget proxy
	 * @param dataTable given data table
	 * @return true if gesture is possible on widget, otherwise false
	 */
	public boolean gesturePossible(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
		return proxy.isUnfiltered(widget);
	}
    
	/**
     * Retrieve actions.
     * @param widget given widget
	 * @param proxy given action widget proxy
	 * @param dataTable given data table
     * @return set of actions 
     */
    public abstract Set<Action> getActions(Widget widget, ActionWidgetProxy proxy, DataTable dataTable);
    
    protected String getStringArgument(int index, DataTable dataTable) {
    	Argument argument = getArguments().get(index);
    	if (argument instanceof PlaceholderArgument) {
    		String columnName = ((PlaceholderArgument)argument).getName();
    		return dataTable.getPlaceholderValue(columnName);
    	}else {
    		if (argument instanceof StringArgument) {
   				return ((StringArgument)argument).getValue();
        	}    		
    	}
    	return null;
    }
    
    protected Boolean getBooleanArgument(int index, DataTable dataTable) {
    	Argument argument = getArguments().get(index);
    	if (argument instanceof PlaceholderArgument) {
    		String columnName = ((PlaceholderArgument)argument).getName();
    		return Boolean.valueOf(dataTable.getPlaceholderValue(columnName));
    	}else {
    		if (argument instanceof BooleanArgument) {
   				return ((BooleanArgument)argument).getValue();
        	}    		
    	}
    	return null;
    }
    
	/**
     * Check gesture.
     * @param dataTable given data table
     * @return list of error descriptions
     */
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		for (Argument argument : getArguments()) {
			if (argument instanceof PlaceholderArgument) {
				String name = ((PlaceholderArgument)argument).getName();
				// check whether the placeholder is a column name of the data table
				if (!dataTable.isColumnName(name)) {
					list.add("Validation error - invalid argument placeholder : " + name + "\n");
				}
			}
		}
		return list;
	}	
	
    @Override
    public abstract String toString();
    
    @Override
    public boolean equals(Object object) {
    	if (object == null) {
    		return false;
    	}
    	if (getClass() != object.getClass()) {
    		return false;
    	}
    	return true;
    }
    
    
    /**
     * Retrieve string representation of arguments.
     * @return arguments
     */
    protected String argumentsToString() {
    	StringBuilder result = new StringBuilder();
    	result.append("(");
    	if (getArguments() != null) {
    		boolean first = true;
    		for (Argument argument : getArguments()) {
	    		if (!first) {
	    			result.append(",");
	    		}
    			result.append(argument.toString());
    			first = false;
	    	}
    	}
    	result.append(")");
    	result.append(System.getProperty("line.separator"));
    	return result.toString();    	
    }
    
}