package strategynodes.filtering;

import java.util.HashMap;
import java.util.Map;

public enum Modifier
{
    VISITED ("visited"),
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited");
    
    public final String                        plainText;
    private static final Map<String, Modifier> FROM_PLAIN_TEXT = new HashMap<>();
    
    private Modifier(String plainText)              {this.plainText = plainText;}
    
    public static Modifier toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (Modifier e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
