package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Class responsible for handling double clicks.
 *
 */
public class DoubleClickGesture extends Gesture {

    /**
     * DoubleClickGesture constructor.
     * @param parameterBase container for parameters
     */
    public DoubleClickGesture(ParameterBase parameterBase) {
    	super(parameterBase);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	if (getParameterBase().size() > 0 && getParameterBase().get(Parameters.UNCHECKED, dataTable)) {    		 
    		// unchecked argument contains value true
    		return super.gesturePossible(widget, proxy, dataTable);
    	}    	
    	return proxy.isClickable(widget);
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	actions.add(ac.leftDoubleClickAt(widget));
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("doubleClick");
   		result.append(getParameterBase().toString());
    	return result.toString();    	
    }
}
