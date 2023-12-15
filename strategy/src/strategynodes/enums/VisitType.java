package strategynodes.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum VisitType
{
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited"),
    VISITED_N ("visited"),
    VISITED_OVER_N ("visited-over"),
    VISITED_UNDER_N ("visited-under");
    
    private final String string;
    private static final Map<String, VisitType> STRINGS = new HashMap<>(); //lookup
    
    VisitType(String plainText) //constructor
    {this.string = plainText;}

    static  //fill lookup with string values
    { Arrays.stream(values()).forEach(e -> STRINGS.put(e.string, e)); }
    
    public static VisitType toEnum(String plainText) {return STRINGS.get(plainText);}

    public String toString() {return this.string;}
}
