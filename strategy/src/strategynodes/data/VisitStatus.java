package strategynodes.data;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.VisitType;

public class VisitStatus
{
    private final VisitType VISIT_TYPE;
    private final boolean intApplies;
    private final Integer visitInt;

    public VisitStatus(VisitType visitType, Integer visitInt)
    {
        this.VISIT_TYPE = visitType;
        this.intApplies =
                (visitType == VisitType.VISITED_N ||
                visitType == VisitType.VISITED_OVER_N ||
                visitType == VisitType.VISITED_UNDER_N);
        this.visitInt = (intApplies) ? visitInt : null;
    }

    public VisitType getVisitType()
    { return VISIT_TYPE; }
    public Boolean actionMatchesVisitStatus(Action action, MultiMap<String, Object> actionsExecuted)
    {
        String actionID = action.get(Tags.AbstractIDCustom);
        return actionMatchesVisitStatus(actionID, actionsExecuted);
    }

    public Boolean actionMatchesVisitStatus(String actionID, MultiMap<String, Object> actionsExecuted)
    {
        boolean actionVisited = (actionsExecuted.containsKey(actionID));

        if(!actionVisited && VISIT_TYPE == VisitType.UNVISITED)
            return true;

        if(VISIT_TYPE == VisitType.LEAST_VISITED)
            return (!actionVisited) ? true : null; //zero times used == least visited, otherwise answer is relative

        if(VISIT_TYPE == VisitType.MOST_VISITED)
            return (!actionVisited) ? false : null; //zero times used == NOT most visited, otherwise answer is relative

        //get number of times used
        int usageCount = (int) actionsExecuted.get(actionID).get(0);

        switch (this.VISIT_TYPE)
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
            return VISIT_TYPE + " " + visitInt;
        else
            return VISIT_TYPE.toString();
    }
}