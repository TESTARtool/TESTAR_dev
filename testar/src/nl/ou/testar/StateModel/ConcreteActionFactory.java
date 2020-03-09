package nl.ou.testar.StateModel;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

public class ConcreteActionFactory {

    public static ConcreteAction createConcreteAction(Action action, AbstractAction abstractAction, String concreteActionId) {
        ConcreteAction concreteAction =  new ConcreteAction(concreteActionId, abstractAction);

        // check if a widget is attached to this action.
        // if so, copy all the attributes to the action
        if (action.get(Tags.OriginWidget, null) != null) {
            setAttributes(concreteAction, action.get(Tags.OriginWidget));
        }
        
        // check if the action as attached a Description (More info than a Widget)
        // if so, set this Description to the current ConcreteAction
        if(action.get(Tags.Desc, null) != null)
        	setSpecificAttribute(concreteAction, Tags.Desc, action.get(Tags.Desc));

        return concreteAction;
    }

    /**
     * Helper method to transfer attribute information from the testar enitities to our own entities.
     * @param widget
     * @param testarWidget
     */
    private static void setAttributes(Widget widget, org.fruit.alayer.Widget testarWidget) {
        for (Tag<?> t : testarWidget.tags()) {
            widget.addAttribute(t, testarWidget.get(t, null));
        }
    }
    
    /**
     * Helper method to set a specific attribute information from the testar enitities to our own entities.
     * @param widget
     * @param tagAttribute
     * @param value
     */
    private static void setSpecificAttribute(Widget widget, Tag tagAttribute, Object value) {
            widget.addAttribute(tagAttribute, value);
    }

}
