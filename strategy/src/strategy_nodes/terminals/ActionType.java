package strategy_nodes.terminals;

import java.util.HashMap;
import java.util.Map;

public enum ActionType
{
    NONE("none"),
    CLICK ("click-action"),
    TYPING ("type-action"),
    DRAG ("drag-action"),
    SCROLL ("scroll-action"),
    HIT_KEY ("hit-key-action");
    
    public final String label;
    private static final Map<String, ActionType> BY_LABEL = new HashMap<>();
    
    private ActionType(String label) {this.label = label;}
    public static ActionType valueOfLabel(String label) {return BY_LABEL.get(label);}
    
    static {for (ActionType e: values()) {BY_LABEL.put(e.label, e);}}
    
    public String toString() {return this.label;}
}
