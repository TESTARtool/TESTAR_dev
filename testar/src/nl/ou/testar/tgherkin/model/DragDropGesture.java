package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Tgherkin DragDropGesture.
 *
 */
public class DragDropGesture extends Gesture {

    /**
     * TypeGesture constructor.
     * @param arguments list of arguments
     */
    public DragDropGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	return super.gesturePossible(widget, proxy, dataTable) && targetWidgetExists(widget, proxy, dataTable);
    }
    
    private boolean targetWidgetExists(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	if (getArguments().size() == 0) {
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
	    		if (widget != targetWidget && proxy.isUnfiltered(targetWidget)) {
	    			return true;
	    		}
	    	}
		}else {
			WidgetCondition widgetCondition = (WidgetCondition)getArguments().get(0).getValue();
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
				if (widget != targetWidget && proxy.isUnfiltered(targetWidget) && widgetCondition.evaluate(proxy, targetWidget, dataTable)) {
					return true;
				}
	    	}
		}
    	return false;
    }
    
    private List<Widget> getTargetWidgets(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	List<Widget> targetWidgets = new ArrayList<Widget>();
    	if (getArguments().size() > 0) {
			WidgetCondition widgetCondition = (WidgetCondition)getArguments().get(0).getValue();
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
				if (widget != targetWidget && proxy.isUnfiltered(targetWidget) && widgetCondition.evaluate(proxy, targetWidget, dataTable)) {
					targetWidgets.add(targetWidget);
				}
	    	}
		}else {
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
	    		if (widget != targetWidget && proxy.isUnfiltered(targetWidget)) {
	    			targetWidgets.add(targetWidget);
	    		}
	    	}
		}
    	return targetWidgets;
    }

    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	List<Widget> targetWidgets = getTargetWidgets(widget, proxy, dataTable); 
		if (getArguments().size() > 0) {
			for (Widget targetWidget : targetWidgets) {
				actions.add(getAction(widget, targetWidget));
			}
		}else {
			// no arguments: generate dragDrop action for one random target widget
			Random random = new Random();
    		int targetNumber  = random.nextInt(targetWidgets.size());
			Widget targetWidget = targetWidgets.get(targetNumber);
			widget.get(Tags.Desc,"Desc unknown");
			actions.add(getAction(widget, targetWidget));
		}
    	return actions;
    }
    
    private Action getAction(Widget widget, Widget targetWidget) {
    	StdActionCompiler ac = new AnnotatingActionCompiler();
		Action action = ac.dragFromTo(widget, targetWidget);
    	action.set(Tags.TargetID, widget.get(Tags.ConcreteID));
		Abstractor abstractor = new StdAbstractor();
		Finder wf = abstractor.apply(widget);	
		action.set(Tags.Targets, Util.newArrayList(wf));
		return action;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("dragDrop");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}
