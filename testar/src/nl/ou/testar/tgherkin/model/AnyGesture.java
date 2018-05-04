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

	
	private List<Gesture> gestures = new ArrayList<Gesture>();
	
    /**
     * AnyGesture constructor.
     * @param arguments list of arguments
     */
    public AnyGesture(List<Argument> arguments) {
    	super(arguments);
    	// pass boolean argument unchecked to the click gestures 
		gestures.add(new ClickGesture(arguments));
		gestures.add(new DoubleClickGesture(arguments));
		gestures.add(new DragSliderGesture(new ArrayList<Argument>()));
		gestures.add(new DragDropGesture(new ArrayList<Argument>()));
		gestures.add(new DropDownAtGesture(new ArrayList<Argument>()));
		gestures.add(new HitKeyGesture(new ArrayList<Argument>()));
		gestures.add(new MouseMoveGesture(new ArrayList<Argument>()));
		gestures.add(new RightClickGesture(new ArrayList<Argument>()));
		gestures.add(new TripleClickGesture(arguments));
		gestures.add(new TypeGesture(new ArrayList<Argument>()));
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
       	// at least one gesture has to be possible
    	for (Gesture gesture: gestures) {
       		if (gesture.gesturePossible(widget, proxy, dataTable)) {
           		return true;
       		}
       	}
    	return false;
    }
    
  
    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	for (Gesture gesture: gestures) {
    		if (gesture.gesturePossible(widget, proxy, dataTable)) {
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
