package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.filtering.ActionType;
import strategynodes.filtering.Filter;
import strategynodes.filtering.Modifier;
import strategynodes.filtering.Relation;

import java.util.*;

public abstract class BaseNode<T>
{
    public abstract T getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted,
                                ArrayList<String> operatingSystems);
    
    @Override
    public abstract String toString();
    
    protected Boolean validActionExists(Modifier modifier, List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        switch(modifier)
                {
                    case LEAST_VISITED: return validLeastVisitedExists(actions, actionsExecuted);
                    case MOST_VISITED: return validMostVisitedExists(actions, actionsExecuted);
                    case UNVISITED: return validUnvisitedExists(actions, actionsExecuted);
                    case VISITED: return validVisitedExists(actions, actionsExecuted);
                };
        return null;
    }
    private boolean validLeastVisitedExists(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int targetCount = (actionsExecuted.size() == 0) ? 0 : Collections.min(actionsExecuted.values());
        
        for(Action action : actions)
        {
            int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
            
            if(count == 0 || count == targetCount)
                return true;
        }
        return false;
    }
    
    private boolean validMostVisitedExists(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        int targetCount = (actionsExecuted.size() == 0) ? 0 : Collections.max(actionsExecuted.values());
        for(Action action : actions)
        {
            int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
            
            if(count == targetCount)
                return true;
        }
        return false;
    }
    
    private boolean validUnvisitedExists(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(!actionIsVisited)
                return true;
        }
        return false;
    }
    
    private boolean validVisitedExists(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(actionIsVisited)
                return true;
        }
        return false;
    }
    protected List<Action> filterByVisitModifier(Modifier modifier, List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        switch(modifier)
                {
                    case LEAST_VISITED: return filterLeastVisited(actions, actionsExecuted);
                    case MOST_VISITED: return filterMostVisited(actions, actionsExecuted);
                    case UNVISITED: return filterUnvisited(actions, actionsExecuted);
                    case VISITED: return filterVisited(actions, actionsExecuted);
                };
        return null;
    }
    
    private List<Action> filterLeastVisited(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        List filteredActions = new ArrayList();
        int targetCount = Integer.MAX_VALUE;
        for(Action action : actions)
        {
            int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
            
            if (count == targetCount)
                filteredActions.add(action);
            else if (count < targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return filteredActions;
    }
    
    private List<Action> filterMostVisited(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        List filteredActions = new ArrayList();
        int targetCount = 0;
        for(Action action : actions)
        {
            int count = actionsExecuted.getOrDefault(action.get(Tags.AbstractIDCustom), 0);
            
            if (count == targetCount)
                filteredActions.add(action);
            else if (count > targetCount)
            {
                targetCount = count;
                filteredActions.clear();
                filteredActions.add(action);
            }
        }
        return filteredActions;
    }
    
    private List<Action> filterUnvisited(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        List filteredActions = new ArrayList();
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(!actionIsVisited)
                filteredActions.add(action);
        }
        return filteredActions;
    }
    
    private List<Action> filterVisited(List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        List filteredActions = new ArrayList();
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(actionIsVisited)
                filteredActions.add(action);
        }
        return filteredActions;
    }

    protected boolean filterAllowsAction(Action action, Filter filter, ActionType actionType)
    {
        return ((filter == Filter.INCLUDE && actionType.actionIsThisType(action)) ||
                (filter == Filter.EXCLUDE && (!actionType.actionIsThisType(action))));
    }

    protected List<Action> filterByActionType(Set<Action> actions, Filter filter, ActionType actionType)
    {
        List<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        {
            if(filterAllowsAction(action, filter, actionType))
                filteredActions.add(action);
        }
        return filteredActions;
    }
    
    protected boolean validRelationExists(Action prevAction, Modifier modifier, Relation relation, List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Widget prevWidget = prevAction.get(Tags.OriginWidget);

        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(relation)
            {
                case CHILD:
                    if(widgetIsChild(prevWidget, widget))
                        return true;
                    break;
                case SIBLING:
                    if(widgetIsSibling(prevWidget, widget))
                        return true;
                    break;
                case SIBLING_OR_CHILD:
                    if(widgetIsChild(prevWidget, widget) || widgetIsSibling(prevWidget, widget))
                        return true;
                    break;
            }
        }
        return false;
    }
    
    protected List<Action> filterByRelation(Action prevAction, List<Action> actions, Relation relation)
    {
        List<Action> filteredActions = new ArrayList<>();
        Widget prevWidget = prevAction.get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(relation)
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
    {
        return (child.parent().get(Tags.AbstractIDCustom).equals(parent.get(Tags.AbstractIDCustom)));
    }
    
    private boolean widgetIsSibling(Widget widget1, Widget widget2)
    { return widget1.parent().get(Tags.AbstractIDCustom).equals(widget2.parent().get(Tags.AbstractIDCustom)); }
}