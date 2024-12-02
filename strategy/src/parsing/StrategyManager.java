package parsing;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.enums.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StrategyManager
{
    private static ParseUtil parseUtil;
    private static MultiMap<String, Object> actionsExecuted = new MultiMap<>();
    private static ArrayList<String> operatingSystem = new ArrayList<>();

    public static void initialize(String strategyFilePath, ArrayList<String> operatingSystems)
    {
        parseUtil = new ParseUtil(strategyFilePath);
        operatingSystem.addAll(operatingSystems);
    }

    public static void beginSequence(State state)
    {
        state.remove(Tags.PreviousAction);
        state.remove(Tags.PreviousActionID);
    }

    public static State getState(State state, State latestState)
    {
        if(latestState == null)
            state.set(Tags.StateChanged, true);
        else
        {
            String previousStateID = latestState.get(Tags.AbstractID);
            boolean stateChanged = ! previousStateID.equals(state.get(Tags.AbstractID));
            state.set(Tags.StateChanged, stateChanged);
        }
        return state;
    }

    public static void recordSelectedAction(State state, Action selectedAction)
    {
        if (selectedAction != null)
        {
            state.set(Tags.PreviousAction, selectedAction);
            state.set(Tags.PreviousActionID, selectedAction.get(Tags.AbstractID, null));
        }
    }

    public static Action selectAction(State state, Set<Action> actions)
    {
        Action selectedAction = parseUtil.selectAction(state, actions);
        String actionID = selectedAction.get(Tags.AbstractID);

        List<Object> entry = getEntry(actionID);
        if(!entry.isEmpty()) // is there's an entry to update
            addOrUpdateEntry(actionID, (int) entry.get(0) + 1, (ActionType) entry.get(1));
        else
            addOrUpdateEntry(actionID, 1, ActionType.getActionType(selectedAction));

        return selectedAction;
    }

    private static List<Object> getEntry(String actionID)
    {
        return actionsExecuted.getOrDefault(actionID, new ArrayList<>()); //create empty entry if not present
    }

    private static void addOrUpdateEntry(String actionID, int usageCount, ActionType actionType)
    {
        ArrayList<Object> updatedEntry = new ArrayList<>();
        updatedEntry.add(usageCount);
        updatedEntry.add(actionType);
        actionsExecuted.replace(actionID, updatedEntry); //replace or create entry
    }

    public static List<Object> getEntryCopy(String actionID)
    {
        return new ArrayList<>(getEntry(actionID)); //should return empty list if nonexistent
    }

    public static int getUsageCount(String actionID)
    {
        List<Object> entry = getEntry(actionID);

        //get the use count from the entry
        return (entry.isEmpty()) ? 0 : (Integer) entry.get(0); //default to zero if empty
    }
    public static ActionType getActionType(String actionID)
    {
        List<Object> entry = getEntry(actionID);
        return  (entry == null) ? null : (ActionType) entry.get(1);
    }

    public static boolean actionsExecutedIsEmpty()
    {
        return (actionsExecuted.isEmpty());
    }

    public static boolean actionsExecutedContainsKey(String actionID)
    {
        return (actionsExecuted.containsKey(actionID));
    }

    public static int actionsExecutedSize()
    {
        return actionsExecuted.size();
    }

    public static Set<String> getActionsExecutedIDs()
    {
        return actionsExecuted.keySet();
    }

    public static ArrayList<String> getOperatingSystem()
    {
        return operatingSystem;
    }

    public static boolean actionIsExecuted(String actionID)
    {
        return actionsExecuted.containsKey(actionID);
    }

//  (visitStatus == null && actionStatus == null) ||    //if neither aspect is filtered on
//  (visit && actionStatus == null) ||                  //if only filter on visit status
//  (visitStatus == null && actionType) ||              //if only filter on action type
//  (visit && actionType)                               //if both aspects are filtered on

    public static List<Action> filterActionsByAppearanceInExecutedList(Set<Action> actions)
    {
        ArrayList<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);
            if(actionsExecuted.containsKey(actionID))
                filteredActions.add(action);
        }
        return filteredActions;
    }

    public static int countExecutedActionsOfCorrectStatus(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        int numActionsRejected = 0;
        for (String pastActionID : actionsExecuted.keySet())
        {
            boolean visitRejected = false;
            boolean actionTypeRejected = false;

            if(visitStatus != null)
            {
                if(!visitStatus.actionIsAllowed(pastActionID))
                    visitRejected = true;
            }

            if(!visitRejected && actionStatus != null) //if visit is already rejected, skip this part
            {
                if (!actionStatus.actionIsAllowed(getActionType(pastActionID))) //action is not of the correct type
                    actionTypeRejected = true;
            }

            if(visitRejected || actionTypeRejected) //if either or both are true, count the action as rejected
                numActionsRejected++;
        }
        return actionsExecuted.size() - numActionsRejected; //return number of actions not rejected
    }

    public static boolean thisExecutedActionExists(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        for (String pastActionID : actionsExecuted.keySet())
        {
            boolean visitRejected = false;
            boolean actionTypeRejected = false;

            if(visitStatus != null)
            {
                if(!visitStatus.actionIsAllowed(pastActionID))
                    visitRejected = true;
            }

            if(!visitRejected && actionStatus != null) //if visit is already rejected, skip this part
            {
                if (!actionStatus.actionIsAllowed(getActionType(pastActionID))) //action is not of the correct type
                    actionTypeRejected = true;
            }

            if(!visitRejected && !actionTypeRejected) //if both are accepted, stop the loop and return true
                return true;
        }
        return false;
    }
}
