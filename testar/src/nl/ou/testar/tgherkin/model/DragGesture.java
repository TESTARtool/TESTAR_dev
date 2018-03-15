package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.Drag;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.Point;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Tgherkin DragGesture.
 *
 */
public class DragGesture extends Gesture {

    /**
     * DragGesture constructor.
     * @param arguments list of arguments
     */
    public DragGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ActionWidgetProxy proxy) {
    	if (super.gesturePossible(widget, proxy)) {
    		return widget.scrollDrags(Gesture.SCROLL_ARROW_SIZE, Gesture.SCROLL_THICKNESS) != null;
    	}
    	return false;
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
		Drag[] drags = widget.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICKNESS);
		if(drags != null){
			for (Drag drag : drags){
				actions.add(ac.dragFromTo(
					new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
					new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
				));
			}
		}
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("drag");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
