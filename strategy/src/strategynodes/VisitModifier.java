package strategynodes;

import java.util.HashMap;
import java.util.Map;

public enum VisitModifier
{
    VISITED ("visited"),
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited");
    
    public final String                               plainText;
    private static final Map<String, VisitModifier> FROM_PLAIN_TEXT = new HashMap<>();
    
    private VisitModifier(String plainText)                   {this.plainText = plainText;}
    
    public static VisitModifier toEnum(String plainText)      {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (VisitModifier e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
