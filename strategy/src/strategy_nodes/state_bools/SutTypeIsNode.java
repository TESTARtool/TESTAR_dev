package strategy_nodes.state_bools;

import strategy_nodes.BaseStrategyNode;
import strategy_nodes.terminals.SutType;

public class SutTypeIsNode extends BaseStrategyNode
{
    private SutType sutType;
    
    public SutTypeIsNode(SutType operatingSystem)
    {
        this.sutType = operatingSystem;
    }
    
    @Override
    public Boolean GetResult(){return null;}
    
//    public Boolean GetResult(State state, Set<Action> actions)
//    {
//        state.
//        long   graphTime = System.currentTimeMillis();
//        Random rnd       = new Random(graphTime);
//        return new ArrayList<>(actions).get(rnd.nextInt(actions.size()));
//    }
    
    @Override
    public String toString() {return "sut-type-is " + sutType.toString();}
}
