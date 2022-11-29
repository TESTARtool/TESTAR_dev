package strategynodes.bool_expr;

import java.util.HashMap;
import java.util.Map;

public enum SutType
{
    WINDOWS ("windows"),
    LINUX ("linux"),
    ANDROID ("android"),
    WEB ("web");
    
    public final String plainText;
    private static final Map<String, SutType> FROM_PLAIN_TEXT = new HashMap<>();
    
    private SutType(String plainText)              {this.plainText = plainText;}
    public static SutType toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (SutType e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
