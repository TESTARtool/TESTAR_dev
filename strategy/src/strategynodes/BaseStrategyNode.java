package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

import java.util.*;

public abstract class BaseStrategyNode<T>
{
    public abstract T getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
    @Override
    public abstract String toString();
    
    protected Boolean validActionExists(VisitedModifier visitedModifier, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        switch(visitedModifier)
                {
                    case LEAST_VISITED: return validLeastVisitedExists(actions, actionsExecuted);
                    case MOST_VISITED: return validMostVisitedExists(actions, actionsExecuted);
                    case UNVISITED: return validUnvisitedExists(actions, actionsExecuted);
                    case VISITED: return validVisitedExists(actions, actionsExecuted);
                };
        return null;
    }
    private boolean validLeastVisitedExists(Set<Action> actions, Map<String, Integer> actionsExecuted)
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
    
    private boolean validMostVisitedExists(Set<Action> actions, Map<String, Integer> actionsExecuted)
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
    
    private boolean validUnvisitedExists(Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(!actionIsVisited)
                return true;
        }
        return false;
    }
    
    private boolean validVisitedExists(Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        for(Action action : actions)
        {
            boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));
            if(actionIsVisited)
                return true;
        }
        return false;
    }
    protected List<Action> filterByVisitedModifier(VisitedModifier visitedModifier, List<Action> actions, Map<String, Integer> actionsExecuted)
    {
        switch(visitedModifier)
                {
                    case LEAST_VISITED: filterLeastVisited(actions, actionsExecuted);
                    case MOST_VISITED: filterMostVisited(actions, actionsExecuted);
                    case UNVISITED: filterUnvisited(actions, actionsExecuted);
                    case VISITED: filterVisited(actions, actionsExecuted);
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
    
    protected boolean validActionTypeExists(Set<Action> actions, Filter filter, ActionType actionType)
    {
        for(Action action : actions)
        {
            boolean actionIsOfType = (actionType.actionIsThisType(action));
            if((filter == Filter.INCLUDE && actionIsOfType) || (filter == Filter.EXCLUDE && (!actionIsOfType)))
                return true;
        }
        return false;
    }
    protected List<Action> filterByActionType(List<Action> actions, Filter filter, ActionType actionType)
    {
        List<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        {
            boolean actionIsOfType = (actionType.actionIsThisType(action));
            if((filter == Filter.INCLUDE && actionIsOfType) || (filter == Filter.EXCLUDE && (!actionIsOfType)))
                filteredActions.add(action);
        }
        return filteredActions;
    }
    
    protected boolean validRelationExists(Action prevAction, Set<Action> actions, RelatedAction relatedAction)
    {
        Widget prevWidget = prevAction.get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(relatedAction)
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
    
    protected List<Action> filterByRelation(Action prevAction, List<Action> actions, RelatedAction relatedAction)
    {
        List<Action> filteredActions = new ArrayList<>();
        Widget prevWidget = prevAction.get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            switch(relatedAction)
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
        return (child.parent().get(Tags.ConcreteIDCustom).equals(parent.get(Tags.ConcreteIDCustom)));
    }
    
    private boolean widgetIsSibling(Widget widget1, Widget widget2)
    { return widget1.parent().get(Tags.ConcreteIDCustom).equals(widget2.parent().get(Tags.ConcreteIDCustom)); }
}