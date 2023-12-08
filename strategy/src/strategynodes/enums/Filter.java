package strategynodes.enums;

import java.util.HashMap;
import java.util.Map;

public enum Filter
{
    INCLUDE ("of-type"),
    EXCLUDE ("not-of-type");
    
    public final String plainText;
    private static final Map<String, Filter> FROM_PLAIN_TEXT = new HashMap<>();
    
    private Filter(String plainText)              {this.plainText = plainText;}
    public static Filter toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (Filter e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
