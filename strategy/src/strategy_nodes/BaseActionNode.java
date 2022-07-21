package strategy_nodes;

public abstract class BaseActionNode<T> extends BaseStrategyNode
{
    protected String name;
    protected int WEIGHT;
    public int GetWeight() {return WEIGHT;}
    
    @Override
    public abstract T GetResult();
    
    @Override
    public String toString() {return String.valueOf(WEIGHT) + " " + name;}
}
