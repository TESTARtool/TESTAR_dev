package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;


/**
 * Tgherkin AnyGesture.
 *
 */
public class AnyGesture extends Gesture {

	
	private static List<Gesture> gestures = new ArrayList<Gesture>(){
		/**
		 * Serial version UID
		 */
		private static final long serialVersionUID = -4478767799881634291L;
	{
		add(new ClickGesture(new ArrayList<Argument>()));
		add(new DoubleClickGesture(new ArrayList<Argument>()));
		add(new DragGesture(new ArrayList<Argument>()));
		add(new DropDownAtGesture(new ArrayList<Argument>()));
		add(new MouseMoveGesture(new ArrayList<Argument>()));
		add(new RightClickGesture(new ArrayList<Argument>()));
		add(new TripleClickGesture(new ArrayList<Argument>()));
		add(new TypeGesture(new ArrayList<Argument>()));
		
	}};
	
    /**
     * AnyGesture constructor.
     * @param arguments list of arguments
     */
    public AnyGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ActionWidgetProxy proxy) {
       	for (Gesture gesture: gestures) {
       		if (gesture.gesturePossible(widget, proxy)) {
           		return true;
       		}
       	}
    	return false;
    }
    
  
    @Override
    public Set<Action> getActions(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	for (Gesture gesture: gestures) {
    		if (gesture.gesturePossible(widget, proxy)) {
        		actions.addAll(gesture.getActions(widget, proxy, dataTable));
    		}
    	}
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("anyGesture");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
