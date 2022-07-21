package strategy_nodes.operators;

import strategy_nodes.BaseStrategyNode;

public class NotEqualOprNode extends BaseStrategyNode<Boolean>
{
    protected BaseStrategyNode left;
    protected BaseStrategyNode right;
    
    public NotEqualOprNode(BaseStrategyNode left, BaseStrategyNode right) {this.left = left;this.right = right;}
    @Override
    public Boolean GetResult() {return left.GetResult() != right.GetResult();}
    
    @Override
    public String toString() {return left.toString() + " != " + right.toString();}
}