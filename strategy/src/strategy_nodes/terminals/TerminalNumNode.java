package strategy_nodes.terminals;

import strategy_nodes.BaseStrategyNode;
public class TerminalNumNode extends BaseStrategyNode<Integer>
{
    private       Integer    NUMBER;
    private final Boolean    IS_NUMBER_ACTIONS_COMPOUND; //flat int>null, simple action->false
    private final NumberRule NUMBER_RULE;
    private final ActionType ACTION_TYPE;
    
    public TerminalNumNode(int number) //flat int
    {
        this.NUMBER                 = number;
        IS_NUMBER_ACTIONS_COMPOUND  = null;
        NUMBER_RULE                 = NumberRule.NONE;
        ACTION_TYPE                 = ActionType.NONE;
    }
    public TerminalNumNode(NumberRule numberRule) //simple action
    {
        this.NUMBER                 = null;
        IS_NUMBER_ACTIONS_COMPOUND  = false;
        NUMBER_RULE                 = numberRule;
        ACTION_TYPE                 = ActionType.NONE;
    }
    public TerminalNumNode(NumberRule numberRule, ActionType actionType) //compound action
    {
        this.NUMBER                 = null;
        IS_NUMBER_ACTIONS_COMPOUND  = true;
        NUMBER_RULE                 = numberRule;
        ACTION_TYPE                 = actionType;
    }
    
    public Integer GetResult() {return NUMBER;} //add enum code later
    
    @Override
    public String toString()
    {
        if(this.IS_NUMBER_ACTIONS_COMPOUND == null) //flat int
            return String.valueOf(NUMBER);
        else if (this.IS_NUMBER_ACTIONS_COMPOUND) //compound action
            return NUMBER_RULE.toString() + " " + ACTION_TYPE.toString();
        else
            return NUMBER_RULE.toString();
    }
}