package parsing;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StrategyManager
{
    private ParseUtil parseUtil;
    private MultiMap<String, Object> actionsExecuted = new MultiMap<>();
    private ArrayList<String> operatingSystems = new ArrayList<>();

    public StrategyManager(String strategyFilePath, ArrayList<String> operatingSystems)
    {
        parseUtil = new ParseUtil(strategyFilePath);
        for (String OS : operatingSystems)
            this.operatingSystems.add(OS);
    }

    public void beginSequence(State state)
    {
        state.remove(Tags.PreviousAction);
        state.remove(Tags.PreviousActionID);
    }

    public State getState(State state, State latestState)
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

    public State recordSelectedAction(State state, Action selectedAction)
    {
        if (selectedAction != null)
        {
            state.set(Tags.PreviousAction, selectedAction);
            state.set(Tags.PreviousActionID, selectedAction.get(Tags.AbstractID, null));
        }
        return state;
    }

    public Action selectAction(State state, Set<Action> actions)
    {
        Action selectedAction = parseUtil.selectAction(state, actions, actionsExecuted, operatingSystems);
        String actionID = selectedAction.get(Tags.AbstractID);

        //get the use count for the action
        List<Object> entry = actionsExecuted.getOrDefault(actionID, null);

        int timesUsed = (entry == null) ? 0 : (Integer) entry.get(0); //default to zero if null
        ActionType actionType = (entry == null) ? ActionType.getActionType(selectedAction) : (ActionType) entry.get(1);

        ArrayList<Object> updatedEntry = new ArrayList<>();
        updatedEntry.add(timesUsed + 1); //increase usage by one
        updatedEntry.add(actionType);
        actionsExecuted.replace(actionID, updatedEntry); //replace or create entry

        return selectedAction;
    }


}
