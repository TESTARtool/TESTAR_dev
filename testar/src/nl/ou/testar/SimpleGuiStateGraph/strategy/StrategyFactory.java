package nl.ou.testar.SimpleGuiStateGraph.strategy;

public interface StrategyFactory {

    /**
     * Get the strategy action selector
     *
     * @return StrategyActionSelector
     */
    StrategyActionSelector getStrategyActionSelector();
}
