package strategy_nodes.operators;

import strategy_nodes.BaseStrategyNode;

public class AndOprNode extends BaseStrategyNode<Boolean>
{
    protected BaseStrategyNode<Boolean> left;
    protected BaseStrategyNode<Boolean> right;
    
    public AndOprNode(BaseStrategyNode left, BaseStrategyNode right) {this.left = left; this.right = right;}
    @Override
    public Boolean GetResult() {return left.GetResult() && right.GetResult();}
    
    @Override
    public String toString()
    {
        return left.toString() + " AND " + right.toString();
    }
}
