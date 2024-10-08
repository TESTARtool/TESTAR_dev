package strategynodes.data;

import org.antlr.v4.runtime.misc.MultiMap;
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
        System.out.println("DEBUG visitstatus init");
        this.visitType = visitType;
        System.out.println("DEBUG visitstatus init visittype: " + visitType);
        this.intApplies =
                (visitType == VisitType.VISITED_N ||
                visitType == VisitType.VISITED_OVER_N ||
                visitType == VisitType.VISITED_UNDER_N);
        System.out.println("DEBUG visitstatus init intapplies: " + intApplies);
        this.visitInt = (intApplies) ? visitInt : null;
        System.out.println("DEBUG visitstatus init visitInt: " + this.visitInt);

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
        System.out.println("DEBUG visitstatus action is allowed");
        System.out.println("DEBUG visitstatus visittype: " + visitType);
        System.out.println("DEBUG visitstatus visitInt: " + visitInt);

//        boolean actionVisited = (actionsExecuted.containsKey(actionID));
        boolean actionVisited = StrategyManager.actionsExecutedContainsKey(actionID);

        if(!actionVisited && visitType == VisitType.UNVISITED)
            return true;

        if(visitType == VisitType.LEAST_VISITED)
            return (!actionVisited) ? true : null; //zero times used == least visited, otherwise answer is relative

        if(visitType == VisitType.MOST_VISITED)
            return (!actionVisited) ? false : null; //zero times used == NOT most visited, otherwise answer is relative

        System.out.println("DEBUG visitstatus get usageCount");
        System.out.println("DEBUG visitstatus actionsExecuted size: ");
        System.out.println(StrategyManager.actionsExecutedSize());


        //get number of times used
//        int usageCount = (int) actionsExecuted.get(actionID).get(0);
        int usageCount = StrategyManager.getUsageCount(actionID);

        System.out.println("DEBUG visitstatus usageCount: ");
        System.out.println(usageCount);

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
        System.out.println("DEBUG tostring");
        System.out.println("DEBUG tostring: " + visitType);
        if (intApplies)
            return visitType + " " + visitInt;
        else
            return visitType.toString();
    }
}