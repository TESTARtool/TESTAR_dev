package strategynodes;

import java.util.HashMap;
import java.util.Map;

public enum VisitedModifier
{
    VISITED ("visited"),
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited");
    
    public final String                               plainText;
    private static final Map<String, VisitedModifier> FROM_PLAIN_TEXT = new HashMap<>();
    
    private VisitedModifier(String plainText)                    {this.plainText = plainText;}
    
    public static VisitedModifier stringToEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (VisitedModifier e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
