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
 * Class responsible for handling drag and drop.
 *
 */
public class DragDropGesture extends Gesture {

    /**
     * TypeGesture constructor.
     * @param parameterBase container for parameters
     */
    public DragDropGesture(ParameterBase parameterBase) {
    	super(parameterBase);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	return super.gesturePossible(widget, proxy, dataTable) && targetWidgetExists(widget, proxy, dataTable);
    }
    
    private boolean targetWidgetExists(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    	if (getParameterBase().size() == 0) {
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
	    		if (widget != targetWidget && proxy.isUnfiltered(targetWidget)) {
	    			return true;
	    		}
	    	}
		}else {
			WidgetCondition widgetCondition = getParameterBase().get(Parameters.WIDGET_CONITION, dataTable);
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
    	if (getParameterBase().size() == 0) {
	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
	    		if (widget != targetWidget && proxy.isUnfiltered(targetWidget)) {
	    			targetWidgets.add(targetWidget);
	    		}
	    	}
		}else {
			WidgetCondition widgetCondition = getParameterBase().get(Parameters.WIDGET_CONITION, dataTable);	    	for (Widget targetWidget : proxy.getTopWidgets(proxy.getState())) {
				if (widget != targetWidget && proxy.isUnfiltered(targetWidget) && widgetCondition.evaluate(proxy, targetWidget, dataTable)) {
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
		if (getParameterBase().size() == 0) {
			// no arguments: generate dragDrop action for one random target widget
			Random random = new Random();
    		int targetNumber  = random.nextInt(targetWidgets.size());
			Widget targetWidget = targetWidgets.get(targetNumber);
			widget.get(Tags.Desc,"Desc unknown");
			actions.add(getAction(widget, targetWidget));
		}else {
			for (Widget targetWidget : targetWidgets) {
				actions.add(getAction(widget, targetWidget));
			}
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
	public List<String> check(DataTable dataTable) {
		List<String> list = new ArrayList<String>();
		WidgetCondition widgetCondition = getParameterBase().get(Parameters.WIDGET_CONITION, dataTable);
		if (widgetCondition != null) {
			list.addAll(widgetCondition.check(dataTable));
		}
		return list;
	}
    
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("dragDrop");
   		result.append(getParameterBase().toString());
    	return result.toString();    	
    }
}
