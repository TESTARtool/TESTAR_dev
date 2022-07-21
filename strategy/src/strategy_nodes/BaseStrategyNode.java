package strategy_nodes;

public abstract class BaseStrategyNode<T>
{
    public abstract T GetResult();
//    public abstract T GetResult(Set<Action> actions);
//    public abstract T GetResult(State state, Set<Action> actions);
    
    @Override
    public abstract String toString();
}
