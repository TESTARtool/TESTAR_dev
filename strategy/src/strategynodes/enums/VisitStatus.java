package strategynodes.enums;

import java.util.HashMap;
import java.util.Map;

public enum VisitStatus
{
    VISITED ("visited"),
    UNVISITED ("unvisited"),
    MOST_VISITED ("most-visited"),
    LEAST_VISITED ("least-visited");
    
    public final String                        plainText;
    private static final Map<String, VisitStatus> FROM_PLAIN_TEXT = new HashMap<>();
    
    private VisitStatus(String plainText)              {this.plainText = plainText;}
    
    public static VisitStatus toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (VisitStatus e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
