package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Tgherkin RightClickGesture.
 *
 */
public class RightClickGesture extends Gesture {

    /**
     * RightClickGesture constructor.
     * @param arguments list of arguments
     */
    public RightClickGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ActionWidgetProxy proxy) {
    	if (super.gesturePossible(widget, proxy)) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	actions.add(ac.rightClickAt(widget));
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("rightClick");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
