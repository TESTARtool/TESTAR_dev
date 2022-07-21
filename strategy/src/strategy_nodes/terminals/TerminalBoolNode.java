package strategy_nodes.terminals;

import strategy_nodes.BaseStrategyNode;

public class TerminalBoolNode extends BaseStrategyNode
{
    private final boolean BOOL;
    
    public TerminalBoolNode(boolean bool) {this.BOOL = bool;}
    public Boolean GetResult()
    {
        return BOOL;
    }
    
    @Override
    public String toString() {return Boolean.toString(BOOL);}
}