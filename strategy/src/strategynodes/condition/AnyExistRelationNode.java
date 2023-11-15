package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseBooleanNode;
import strategynodes.filtering.Modifier;
import strategynodes.filtering.Relation;

import java.util.*;

public class AnyExistRelationNode extends BaseBooleanNode
{
    private final Modifier MODIFIER;
    private final Relation RELATION;
    
    public AnyExistRelationNode(Modifier modifier, Relation relation)
    {
        this.MODIFIER = modifier;
        this.RELATION = relation;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);
        boolean validActionFound = false;

        if(prevAction != null)
        {
            List<Action> filteredActions = new ArrayList<>(actions);

            if(MODIFIER != null) //filter by modifier
                filteredActions = filterByVisitModifier(MODIFIER, filteredActions, actionsExecuted);

            //check by relation
            validActionFound = validRelationExists(prevAction, MODIFIER, RELATION, filteredActions, actionsExecuted);
        }
        return validActionFound;
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("any-exist");
        if(MODIFIER != null)
            joiner.add(MODIFIER.toString());
        if(RELATION != null)
            joiner.add(RELATION.toString());
        return joiner.toString();
    }
}