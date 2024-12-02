package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import parsing.StrategyManager;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;

import java.util.Set;
import java.util.StringJoiner;

public class NrOfPreviousActionsNode extends BaseNode implements IntegerNode
{
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;

    public NrOfPreviousActionsNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions)
    {
        return StrategyManager.countExecutedActionsOfCorrectStatus(visitStatus, actionStatus);
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-previous");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}