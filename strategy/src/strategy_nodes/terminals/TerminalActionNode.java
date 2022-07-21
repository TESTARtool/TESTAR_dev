package strategy_nodes.terminals;

import strategy_nodes.BaseStrategyNode;


public class TerminalActionNode extends BaseStrategyNode
{
    private final int        WEIGHT;
    private final boolean    IS_COMPOUND;
    private final ActionRule ACTION_RULE;
    private final ActionType ACTION_TYPE;
    
    public TerminalActionNode(int weight, ActionRule actionRule)
    {
        this.WEIGHT      = weight;
        this.ACTION_RULE = actionRule;
        this.ACTION_TYPE = ActionType.NONE;
        IS_COMPOUND      = false;
    }
    public TerminalActionNode(int weight, ActionRule actionRule, ActionType actionType)
    {
        this.WEIGHT      = weight;
        this.ACTION_RULE = actionRule;
        this.ACTION_TYPE = actionType;
        IS_COMPOUND      = true;
    }
    
    public int GetWeight(){return WEIGHT;}
    public boolean IsCompoundAction(){return IS_COMPOUND;}
    public ActionRule GetActionRule(){return ACTION_RULE;}
    public ActionType GetActionType(){return ACTION_TYPE;}
    
    @Override
    public BaseStrategyNode GetResult()
    {
        return null;
    } //todo
    
    @Override
    public String toString()
    {
        String str = String.valueOf(WEIGHT) + " " + ACTION_RULE.toString();
        if(IS_COMPOUND) str += " " + ACTION_TYPE.toString();
        return str;
    }
}
