package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;

/**
 * Tgherkin DropDownAtGesture.
 *
 */
public class DropDownAtGesture extends Gesture {

    /**
     * DropDownAtGesture constructor.
     * @param arguments list of arguments
     */
    public DropDownAtGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	actions.add(ac.dropDownAt(widget));
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("dropDownAt");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
