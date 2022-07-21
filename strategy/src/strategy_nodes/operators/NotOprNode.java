package strategy_nodes.operators;

import strategy_nodes.BaseStrategyNode;

public class NotOprNode extends BaseStrategyNode<Boolean>
{
    protected BaseStrategyNode<Boolean> expr;
    
    public NotOprNode(BaseStrategyNode right) {this.expr = right;}
    @Override
    public Boolean GetResult() { return !expr.GetResult();}
    
    @Override
    public String toString() {return "NOT " + expr.toString();}
}