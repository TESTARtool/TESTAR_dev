package strategynodes.enums;

import java.util.HashMap;
import java.util.Map;

public enum RelationType
{
    SIBLING ("sibling"),
    CHILD ("child"),
    SIBLING_OR_CHILD ("sibling-or-child");
    
    public final String                        plainText;
    private static final Map<String, RelationType> FROM_PLAIN_TEXT = new HashMap<>();
    
    private RelationType(String plainText)              {this.plainText = plainText;}
    public static RelationType toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (RelationType e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
