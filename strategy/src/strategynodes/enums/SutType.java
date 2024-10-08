package strategynodes.enums;

import parsing.StrategyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum SutType
{
    WINDOWS ("windows")
            {
                @Override
                public boolean sutIsThisType()
                {
                    for(String OS : StrategyManager.getOperatingSystem())
                        if(OS.contains("Windows"))
                            return true;
                    return false;
                }
            },
    UNIX ("unix")
            {
                @Override
                public boolean sutIsThisType()
                {
                    for(String OS : StrategyManager.getOperatingSystem())
                        if(OS.contains("Unix"))
                            return true;
                    return false;
                }
            },
    IOS ("ios")
            {
                @Override
                public boolean sutIsThisType()
                {
                    for(String OS : StrategyManager.getOperatingSystem())
                        if(OS.contains("iOS"))
                            return true;
                    return false;
                }
            },
    ANDROID ("android")
            {
                @Override
                public boolean sutIsThisType()
                {
                    for(String OS : StrategyManager.getOperatingSystem())
                        if(OS.contains("Android"))
                            return true;
                    return false;
                }
            },
    WEB ("web")
            {
                @Override
                public boolean sutIsThisType()
                {
                    for(String OS : StrategyManager.getOperatingSystem())
                        if(OS.contains("WebDriver"))
                            return true;
                    return false;
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
