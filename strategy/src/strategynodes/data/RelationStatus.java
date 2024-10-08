package strategynodes.data;

import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.RelationType;
import java.util.StringJoiner;

public class RelationStatus
{
    private final boolean include;
    private final RelationType relationType;

    public RelationStatus(Filter filter, RelationType relationType)
    {
        include = (filter == Filter.INCLUDE || filter == null); //default to include if not present
        this.relationType = relationType;
    }

    public RelationType getRelationType()
    { return relationType; }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        if(include)
            joiner.add(Filter.INCLUDE.toString());
        else
            joiner.add(Filter.EXCLUDE.toString());
        joiner.add(relationType.toString());
        return joiner.toString();
    }
}