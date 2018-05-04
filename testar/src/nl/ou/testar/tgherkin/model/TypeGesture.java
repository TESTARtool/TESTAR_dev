package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;

/**
 * Tgherkin TypeGesture.
 *
 */
public class TypeGesture extends Gesture {

    /**
     * TypeGesture constructor.
     * @param arguments list of arguments
     */
    public TypeGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
   		return proxy.isTypeable(widget);
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
		if (getArguments().size() > 0) {
			actions.add(ac.clickTypeInto(widget, getStringArgument(0, dataTable)));
		}else {
			// no arguments: generate random text
			actions.add(ac.clickTypeInto(widget, proxy.getRandomText(widget)));
		}
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("type");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
