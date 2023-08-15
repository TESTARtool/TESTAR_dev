package strategynodes.condition;

import java.util.*;

public enum BooleanOperator
{
    NOT ("NOT", "!", "~")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return ! right;} //left will not be considered
            },
    AND ("AND", "&&", "&")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left && right;}
            },
    XOR ("XOR", "^")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left ^ right;}
            },
    OR ("OR", "||", "|")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return left || right;}
            },
    IS ("IS", "EQUALS")
            {
                @Override
                public boolean getResult(Boolean left, Boolean right)
                {return Objects.equals(left, right);}
            };
    
    public abstract boolean getResult(Boolean left, Boolean right);
    
//    public final ArrayList<String> strings;
    private final String mainString;
    private final String altString1;
    private final String altString2;
    
    public String getMainString() {return mainString;}
    public String getAltString1() {return altString1;}
    public String getAltString2() {return altString2;}
    
    private static final Map<String, BooleanOperator> FROM_STRING = new HashMap<>();
    
    //todo: test this
    //from https://howtodoinjava.com/java/enum/enum-with-multiple-values/
    public static Optional<BooleanOperator> getBoolOperatorByString(String value) {
        return Arrays.stream(BooleanOperator.values())
                .filter(
                        operator -> operator.mainString.equals(value)
                        || operator.altString1.equals(value)
                        || operator.altString2.equals(value))
                .findFirst();
    }
    
    BooleanOperator(String ... strings)
    {
        this.mainString = strings[0];
        this.altString1 = strings[1];
        this.altString2 = (strings.length > 2) ? strings[2] : strings[0]; //string can't be empty, so duplicate main string
    }
    public String toString() { return getMainString(); }
//
//    public static BooleanOperator toEnum(String string) { return FROM_STRING.get(string); }
//
//    static
//    {
////        Arrays.stream(values()).forEach(e -> FROM_STRING.put(e.strings, e));
//        for(BooleanOperator value : values())
//        {
//            for(String string : value.strings)
//                FROM_STRING.put(string, value);
//        }
//    }
}
