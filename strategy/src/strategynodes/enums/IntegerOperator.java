package strategynodes.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum IntegerOperator
{
    LT ("<")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left < right;}
            },
    LE ("<=")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left <= right;}
            },
    GT (">")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left > right;}
            },
    GE (">=")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left >= right;}
            },
    EQ ("==")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left == right;}
            },
    NE ("!=")
            {
                @Override
                public boolean getResult(Integer left, Integer right)
                {return left != right;}
            };
    
    public abstract boolean getResult(Integer left, Integer right);
    
    public final String                               string;
    private static final Map<String, IntegerOperator> FROM_STRING = new HashMap<>();
    
    IntegerOperator(String string)                      {this.string = string;}
    
    public static IntegerOperator toEnum(String string) { return FROM_STRING.get(string); }
    public String toString()                            { return this.string; }
    
    static
    { Arrays.stream(values()).forEach(e -> FROM_STRING.put(e.string, e)); }
}
