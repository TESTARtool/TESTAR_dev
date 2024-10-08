package strategynodes.data;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.Filter;
import strategynodes.enums.ActionType;
import java.util.StringJoiner;

public class ActionStatus
{
    private final boolean include;
    private final ActionType actionType;

    public ActionStatus(Filter filter, ActionType actionType)
    {
        include = (filter == Filter.INCLUDE || filter == null); //default to include if not present
        this.actionType = actionType;
    }

    public ActionType getActionType()
    { return actionType; }

    public Boolean actionIsAllowed(ActionType actionType)
    {
        return (include && (this.actionType == actionType)) ||
                (!include && (this.actionType != actionType));
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        if(include)
            joiner.add(Filter.INCLUDE.toString());
        else
            joiner.add(Filter.EXCLUDE.toString());
        joiner.add(actionType.toString());
        return joiner.toString();
    }
}