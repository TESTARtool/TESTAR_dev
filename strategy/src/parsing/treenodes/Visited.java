package parsing.treenodes;

import java.util.HashMap;
import java.util.Map;

public enum Visited
{
    VISITED ("visited"),
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited");
    
    public final String                       plainText;
    private static final Map<String, Visited> FROM_PLAIN_TEXT = new HashMap<>();
    
    private Visited(String plainText) {this.plainText = plainText;}
    
    public static Visited stringToEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (Visited e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
