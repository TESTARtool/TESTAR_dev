package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;

import java.util.Collection;
import java.util.Set;
import java.util.StringJoiner;

public class PreviousExistNode extends BaseNode implements BooleanNode
{
    private VisitStatus visitStatus;
    private ActionStatus actionStatus;

    public PreviousExistNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if (prevAction == null)
            return false;

        Collection<Action> filteredActions = filterActionsByExecution(actions); //only keep actions that are present in the executedActions list
        if (filteredActions.isEmpty()) //if nothing has made it through the filters
            return false;

        int numActionsExist = StrategyManager.countExecutedActionsOfCorrectStatus(visitStatus, actionStatus);

        return (numActionsExist > 0);
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("previous-exist");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}