package strategynodes.bool_expr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BoolOperator
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
    
    public final String                            string;
    private static final Map<String, BoolOperator> FROM_STRING = new HashMap<>();
    
    BoolOperator(String string)                      {this.string = string;}
    
    public static BoolOperator toEnum(String string) { return FROM_STRING.get(string); }
    public String toString()                         { return this.string; }
    
    static
    { Arrays.stream(values()).forEach(e -> FROM_STRING.put(e.string, e)); }
}
