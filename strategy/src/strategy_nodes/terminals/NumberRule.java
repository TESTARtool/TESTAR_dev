package strategy_nodes.terminals;

import java.util.HashMap;
import java.util.Map;

public enum NumberRule
{
    NONE("none"),
    TOTAL_NUMBER_OF_ACTIONS("total-number-of-actions"),
    TOTAL_NUMBER_OF_UNEXECUTED_ACTIONS("total-number-of-unexecuted-actions"),
    TOTAL_NUMBER_OF_PREVIOUS_UNEXECUTED_ACTIONS("total-number-of-previous-executed-actions"),
    NUMBER_OF_PREVIOUS_UNEXECUTED_ACTIONS_OF_TYPE("number-of-previous-executed-actions-of-type"),
    NUMBER_OF_ACTIONS_OF_TYPE("number-of-actions-of-type"),
    NUMBER_OF_UNEXECUTED_ACTIONS_OF_TYPE("number-of-unexecuted-actions-of-type");
    
    public final String label;
    private static final Map<String, NumberRule> BY_LABEL = new HashMap<>();
    
    private NumberRule(String label)                    {this.label = label;}
    public static NumberRule valueOfLabel(String label) {return BY_LABEL.get(label);}
    
    static {for (NumberRule e: values()) {BY_LABEL.put(e.label, e);}}
    
    public String toString() {return this.label;}
}
