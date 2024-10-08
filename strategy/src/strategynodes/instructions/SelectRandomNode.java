package strategynodes.instructions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.ActionStatus;
import strategynodes.data.RelationStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.RelationFilter;
import strategynodes.filters.VisitFilter;
import strategynodes.BaseNode;
import strategynodes.data.Weight;
import java.util.*;

public class SelectRandomNode extends BaseNode implements ActionNode
{
    public final Weight weight;
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;
    private final RelationStatus relationStatus;
    ArrayList<Action> filteredActions = new ArrayList<>();

    public SelectRandomNode(Integer weight, VisitStatus visitStatus, ActionStatus actionStatus)
    {
        System.out.println("DEBUG random node init 1");

        this.weight = new Weight(weight);
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
        this.relationStatus = null;
    }
    public SelectRandomNode(Integer weight, VisitStatus visitStatus, RelationStatus relationStatus)
    {
        System.out.println("DEBUG random node init 2");

        this.weight = new Weight(weight);
        this.visitStatus = visitStatus;
        this.actionStatus = null;
        this.relationStatus = relationStatus;
    }
//    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, ActionType actionType)
//    {
//        this.weight = new Weight(weight);
//        System.out.println("DEBUG random node init 1");
//
//        if(visitStatus != null)
//        {
//            System.out.println("DEBUG random node init 1: visitstatus is not NULL");
//            visitFilter = new VisitFilter(visitStatus);
//        }
//        if(filter != null && actionType != null)
//            actionTypeFilter = new ActionTypeFilter(filter, actionType);
//        filteredActions = new ArrayList<>();
//    }
//    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, Relation relation)
//    {
//        this.weight = new Weight(weight);
//        System.out.println("DEBUG random node init 2");
//
//        if(visitStatus != null)
//        {
//            System.out.println("DEBUG random node init 2: visitstatus is not NULL");
//            visitFilter = new VisitFilter(visitStatus);
//        }
//        if(filter != null && relation != null)
//            relationFilter = new RelationFilter(filter, relation);
//        filteredActions = new ArrayList<>();
//    }

    //todo: check if it works correctly
    public Action getResult(State state, Set<Action> actions)
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        filteredActions = new ArrayList<>(actions); // copy the actions list

        System.out.println("DEBUG selectRandomNode getResult");
        if(visitStatus != null)
        System.out.println("DEBUG selectRandomNode visitStatus: " + visitStatus);

        if(visitStatus != null)
            filteredActions = VisitFilter.filterAvailableActions(visitStatus, filteredActions);

        if(actionStatus != null)
            filteredActions = ActionTypeFilter.filter(actionStatus, filteredActions);
        else if(relationStatus != null)
            filteredActions = RelationFilter.filter(relationStatus, state.get(Tags.PreviousAction, null), filteredActions);

        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(filteredActions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public int GetWeight()
    {
        return weight.GetWeight();
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(weight.toString());
        joiner.add("select-random");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        else if (relationStatus != null)
            joiner.add(relationStatus.toString());
        return joiner.toString();
    }
}
