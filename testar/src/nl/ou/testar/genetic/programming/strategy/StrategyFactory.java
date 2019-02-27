package nl.ou.testar.genetic.programming.strategy;

public interface StrategyFactory {

    /**
     * Get the strategy action selector
     *
     * @return StrategyActionSelector
     */
    StrategyActionSelector getStrategyActionSelector();
}
