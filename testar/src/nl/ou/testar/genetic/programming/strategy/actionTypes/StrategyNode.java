package nl.ou.testar.genetic.programming.strategy.actionTypes;

import java.util.List;


public abstract class StrategyNode {
    private List<StrategyNode> children;

    public StrategyNode(final List<StrategyNode> children) {
        this.children = children;
    }

    public void print(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("-");
        }
        System.out.println(this.getClass().getSimpleName());
        children.forEach(strategyNode -> strategyNode.print(level + 1));
    }

}
