package strategynodes.data;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;
import strategynodes.enums.VisitType;

public class VisitStatus
{
    private final VisitType visitType;
    private final boolean intApplies;
    private final Integer visitInt;

    public VisitStatus(VisitType visitType, Integer visitInt)
    {
        this.visitType = visitType;
        this.intApplies =
                (visitType == VisitType.VISITED_N ||
                visitType == VisitType.VISITED_OVER_N ||
                visitType == VisitType.VISITED_UNDER_N);
        this.visitInt = (intApplies) ? visitInt : null;
    }

    public VisitType getVisitType()
    { return visitType; }
    public Boolean actionIsAllowed(Action action)
    {
        String actionID = action.get(Tags.AbstractID);
        return actionIsAllowed(actionID);
    }

    public Boolean actionIsAllowed(String actionID)
    {
        // if this action is the first to be executed, it will always be new
        boolean actionVisited = !StrategyManager.actionsExecutedIsEmpty() && StrategyManager.actionsExecutedContainsKey(actionID);

        if(!actionVisited && visitType == VisitType.UNVISITED)
            return true;

        if(visitType == VisitType.LEAST_VISITED)
            return (!actionVisited) ? true : null; //zero times used == least visited, otherwise answer is relative

        if(visitType == VisitType.MOST_VISITED)
            return (!actionVisited) ? false : null; //zero times used == NOT most visited, otherwise answer is relative

        //get number of times used
        int usageCount = StrategyManager.getUsageCount(actionID);

        switch (this.visitType)
        {
            case VISITED_N:
                return (usageCount == visitInt);
            case VISITED_OVER_N:
                return (usageCount > visitInt);
            case VISITED_UNDER_N:
                return (usageCount < visitInt);
            default:
                return false; //shouldn't ever happen
        }
    }

    public String toString()
    {
        if (intApplies)
            return visitType + " " + visitInt;
        else
            return visitType.toString();
    }
}