package strategynodes;

import java.util.HashMap;
import java.util.Map;

public enum RelatedAction
{
    SIBLING ("sibling-action"),
    CHILD ("child-action"),
    SIBLING_OR_CHILD ("sibling-or-child-action");
    
    public final String                             plainText;
    private static final Map<String, RelatedAction> FROM_PLAIN_TEXT = new HashMap<>();
    
    private RelatedAction(String plainText)              {this.plainText = plainText;}
    public static RelatedAction toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (RelatedAction e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
