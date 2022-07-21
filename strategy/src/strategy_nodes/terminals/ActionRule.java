package strategy_nodes.terminals;

import java.util.HashMap;
import java.util.Map;

public enum ActionRule
{
    PREVIOUS_ACTION("previous-action"),
    RANDOM_ACTION("random-action"),
    RANDOM_ACTION_OF_TYPE("random-action-of-type"),
    RANDOM_UNEXECUTED_ACTION("random-unexecuted-action"),
    RANDOM_UNEXECUTED_ACTION_OF_TYPE("random-unexecuted-action-of-type"),
    RANDOM_ACTION_OTHER_THAN("random-action-other-than"),
    RANDOM_UNEXECUTED_ACTION_OTHER_THAN("random-unexecuted-action-other-than"),
    RANDOM_LEAST_EXECUTED_ACTION("random-least-executed-action"),
    RANDOM_MOST_EXECUTED_ACTION("random-most-executed-action");
    
    public final String label;
    private static final Map<String, ActionRule> BY_LABEL = new HashMap<>();
    
    private ActionRule(String label)                    {this.label = label;}
    public static ActionRule valueOfLabel(String label) {return BY_LABEL.get(label);}
    
    static {for (ActionRule e: values()) {BY_LABEL.put(e.label, e);}}
    
    public String toString() {return this.label;}
}