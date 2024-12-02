package strategynodes.filters;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.data.RelationStatus;

import java.util.ArrayList;
import java.util.List;

public class RelationFilter
{
    private static ArrayList<Action> filteredActions = new ArrayList<Action>();

    private RelationFilter() {}  //ensure it can't be instantiated
    public static ArrayList<Action> filter(RelationStatus relationStatus, Action prevAction, List<Action> actions)
    {
        filteredActions.clear();

        Widget prevWidget = prevAction.get(Tags.OriginWidget);

        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(relationStatus.getRelationType())
            {
                case CHILD:
                    if(widgetIsChild(prevWidget, widget))
                        filteredActions.add(action);
                    break;
                case SIBLING:
                    if(widgetIsSibling(prevWidget, widget))
                        filteredActions.add(action);
                    break;
                case SIBLING_OR_CHILD:
                    if(widgetIsChild(prevWidget, widget) || widgetIsSibling(prevWidget, widget))
                        filteredActions.add(action);
                    break;
            }
        }
        return filteredActions;
    }
    private static boolean widgetIsChild(Widget parent, Widget child)
    { return (child.parent().get(Tags.AbstractID).equals(parent.get(Tags.AbstractID))); }

    private static boolean widgetIsSibling(Widget widget1, Widget widget2)
    { return widget1.parent().get(Tags.AbstractID).equals(widget2.parent().get(Tags.AbstractID)); }
}
