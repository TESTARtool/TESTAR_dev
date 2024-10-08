package parsing;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.enums.ActionType;
import strategynodes.enums.SutType;

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

    public static State recordSelectedAction(State state, Action selectedAction)
    {
        if (selectedAction != null)
        {
            state.set(Tags.PreviousAction, selectedAction);
            state.set(Tags.PreviousActionID, selectedAction.get(Tags.AbstractID, null));
        }
        return state;
    }

    public static Action selectAction(State state, Set<Action> actions)
    {
        Action selectedAction = parseUtil.selectAction(state, actions);
        String actionID = selectedAction.get(Tags.AbstractID);

        List<Object> entry = getEntry(actionID);
        if(!entry.isEmpty()) // is there's an entry to update
        {
            ArrayList<Object> updatedEntry = new ArrayList<>();
            updatedEntry.add(getUsageCount(entry) + 1);
            updatedEntry.add(entry.get(1));
            actionsExecuted.replace(actionID, updatedEntry); //replace or create entry
        }
        else
        {
            ArrayList<Object> newEntry = new ArrayList<>();
            newEntry.add(1);
            newEntry.add(ActionType.getActionType(selectedAction));
            actionsExecuted.replace(actionID, newEntry);
        }

        return selectedAction;
    }

    private static List<Object> getEntry(String actionID)
    {
        return actionsExecuted.getOrDefault(actionID, new ArrayList<>()); //create empty entry if not present
    }
    private static int getUsageCount(List<Object> entry)
    {
        //get the use count from the entry
        return (entry == null) ? 0 : (Integer) entry.get(0); //default to zero if null
    }

    public static List<Object> getEntryCopy(String actionID)
    {
        return new ArrayList<>(getEntry(actionID)); //should return empty list if nonexistent
    }

    public static int getUsageCount(String actionID)
    {
        //get the use count from the entry
        return getUsageCount(getEntry(actionID));
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
    
}
