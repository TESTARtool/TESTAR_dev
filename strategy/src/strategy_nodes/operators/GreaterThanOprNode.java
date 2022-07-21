package strategy_nodes.operators;

import strategy_nodes.BaseStrategyNode;

public class GreaterThanOprNode extends BaseStrategyNode
{
    protected BaseStrategyNode left;
    protected BaseStrategyNode right;
    
    public GreaterThanOprNode(BaseStrategyNode left, BaseStrategyNode right) {this.left = left;this.right = right;}
    @Override
    public Boolean GetResult() {return (int)left.GetResult() > (int)right.GetResult();}
    
    @Override
    public String toString() {return left.toString() + " > " + right.toString();}
}
