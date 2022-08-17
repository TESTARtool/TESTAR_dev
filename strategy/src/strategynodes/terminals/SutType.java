package strategynodes.terminals;

import java.util.HashMap;
import java.util.Map;

public enum SutType
{
    NONE("none"),
    WINDOWS ("windows"),
    LINUX ("linux"),
    ANDROID ("android"),
    WEB ("web");
    
    public final String label;
    private static final Map<String, SutType> BY_LABEL = new HashMap<>();
    
    private SutType(String label) {this.label = label;}
    public static SutType valueOfLabel(String label) {return BY_LABEL.get(label);}
    
    static {for (SutType e: values()) {BY_LABEL.put(e.label, e);}}
    
    public String toString() {return this.label;}
}
