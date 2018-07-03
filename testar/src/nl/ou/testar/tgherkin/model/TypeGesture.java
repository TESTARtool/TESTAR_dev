package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.CompoundAction.Builder;
import org.fruit.alayer.devices.KBKeys;

/**
 * Class responsible for handling typing in text.
 *
 */
public class TypeGesture extends Gesture {

    /**
     * TypeGesture constructor.
     * @param parameterBase container for parameters
     */
    public TypeGesture(ParameterBase parameterBase) {
    	super(parameterBase);
   	}
	
    
    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
   		return proxy.isTypeable(widget);
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
		if (getParameterBase().size() > 0) {
			String text = getParameterBase().get(Parameters.TEXT, dataTable);
			if ("".equals(text)) {
				// type action does not allow typing of an empty string
				// use hitkey to clear the existing text on the screen
				actions.add(clearScreenText(widget));
			} else {
				actions.add(ac.clickTypeInto(widget, getParameterBase().get(Parameters.TEXT, dataTable)));				
			}

		} else {
			// no arguments: generate random text
			actions.add(ac.clickTypeInto(widget, proxy.getRandomText(widget)));
		}
    	return actions;
    }
    
    private Action clearScreenText(Widget widget) {
    	// click on widget and apply Shift-Home, Del and Escape actions.
		Builder builder = new CompoundAction.Builder();
		StdActionCompiler ac = new AnnotatingActionCompiler();
		builder.add(ac.leftClickAt(widget),1);
		List<KBKeys> keys = new ArrayList<KBKeys>();
		keys.add(KBKeys.VK_SHIFT);
		keys.add(KBKeys.VK_HOME);
		builder.add(ac.hitShortcutKey(keys), 1);
		keys.clear();
		keys.add(KBKeys.VK_DELETE);
		builder.add(ac.hitShortcutKey(keys), 1);
		// get rid of combo-box options in pop-up, if any
		keys.clear();
		keys.add(KBKeys.VK_ESCAPE);  
		builder.add(ac.hitShortcutKey(keys), 1);
		Action action = builder.build();
		Abstractor abstractor = new StdAbstractor();
		Finder wf = abstractor.apply(widget);
		action.set(Tags.Targets, Util.newArrayList(wf));
		action.set(Tags.TargetID, widget.get(Tags.ConcreteID)); 		
		action.set(Tags.Desc, "Type '' into '" + widget.get(Tags.Desc, "<no description>") + "'"); 
		action.set(Tags.Role, ActionRoles.ClickTypeInto);	
		return action;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("type");
   		result.append(getParameterBase().toString());   		
    	return result.toString();    	
    }
}
