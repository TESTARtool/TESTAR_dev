package strategynodes.filter;

import java.util.HashMap;
import java.util.Map;

public enum Relation
{
    SIBLING ("sibling"),
    CHILD ("child"),
    SIBLING_OR_CHILD ("sibling-or-child");
    
    public final String                        plainText;
    private static final Map<String, Relation> FROM_PLAIN_TEXT = new HashMap<>();
    
    private Relation(String plainText)              {this.plainText = plainText;}
    public static Relation toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (Relation e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
