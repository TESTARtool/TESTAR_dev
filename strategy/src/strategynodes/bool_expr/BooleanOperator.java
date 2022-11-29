package strategynodes.bool_expr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BooleanOperator
{
    NOT ("NOT")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return ! right;} //left will not be considered
            },
    AND ("AND")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left && right;}
            },
    XOR ("XOR")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left ^ right;}
            },
    OR ("OR")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left || right;}
            };
    
    public abstract boolean getResult(Boolean left, Boolean right);
    
    public final String                               string;
    private static final Map<String, BooleanOperator> FROM_STRING = new HashMap<>();
    
    BooleanOperator(String string)                      {this.string = string;}
    
    public static BooleanOperator toEnum(String string) { return FROM_STRING.get(string); }
    public String toString()                            { return this.string; }
    
    static
    { Arrays.stream(values()).forEach(e -> FROM_STRING.put(e.string, e)); }
}
