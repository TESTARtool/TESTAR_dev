package strategynodes.condition;

import java.util.HashMap;
import java.util.Map;

public enum SutType
{
    WINDOWS ("windows")
            {
                @Override
                public boolean sutIsThisType()
                {
                    return (System.getProperty("os.name").contains("Windows"));
                }
            },
    LINUX ("linux")
            {
                @Override
                public boolean sutIsThisType()
                {
                    return (System.getProperty("os.name").contains("Linux"));
                }
            },
    ANDROID ("android")
            {
                @Override
                public boolean sutIsThisType()
                {
                    return false; //todo: implement
                }
            },
    WEB ("web")
            {
                @Override
                public boolean sutIsThisType()
                {
                    return false; //todo: implement
                }
            };

    public abstract boolean sutIsThisType();
    
    public final String plainText;
    private static final Map<String, SutType> FROM_PLAIN_TEXT = new HashMap<>();
    
    private SutType(String plainText)              {this.plainText = plainText;}
    public static SutType toEnum(String plainText) {return FROM_PLAIN_TEXT.get(plainText);}
    
    static {for (SutType e: values()) {FROM_PLAIN_TEXT.put(e.plainText, e);}}
    
    public String toString() {return this.plainText;}
}
