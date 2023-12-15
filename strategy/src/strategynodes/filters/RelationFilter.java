package strategynodes.filters;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.enums.Filter;
import strategynodes.enums.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class RelationFilter
{
    private final boolean include;
    private final Relation RELATION;
    private ArrayList<Action> filteredActions;

    public RelationFilter(Filter filter, Relation relation)
    {
        include = (filter == Filter.INCLUDE || filter == null); //default to include if not present
        this.RELATION = relation;
        filteredActions = new ArrayList<Action>();
    }
    public ArrayList<Action> filter(Action prevAction, List<Action> actions)
    {
        filteredActions.clear();

        Widget prevWidget = prevAction.get(Tags.OriginWidget);

        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(RELATION)
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
    private boolean widgetIsChild(Widget parent, Widget child)
    { return (child.parent().get(Tags.AbstractIDCustom).equals(parent.get(Tags.AbstractIDCustom))); }

    private boolean widgetIsSibling(Widget widget1, Widget widget2)
    { return widget1.parent().get(Tags.AbstractIDCustom).equals(widget2.parent().get(Tags.AbstractIDCustom)); }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        if(include)
            joiner.add(Filter.INCLUDE.toString());
        else
            joiner.add(Filter.EXCLUDE.toString());
        joiner.add(RELATION.toString());
        return joiner.toString();
    }
}
