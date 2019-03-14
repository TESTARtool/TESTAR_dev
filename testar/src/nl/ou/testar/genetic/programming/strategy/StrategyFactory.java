package nl.ou.testar.genetic.programming.strategy;

public interface StrategyFactory {

    /**
     * Get the strategy action selector
     *
     * @return StrategyActionSelector
     */
    StrategyActionSelector getStrategyActionSelector();

    /**
     * Print metrics
     */
    void printMetrics();


    /**
     * Returns inputs for
     *
     * @param inputFile - File to read inputs from. If a line starts with a # it is considered a comment
     * @return inputs for type actions
     */
    String[] getTextInputsFromFile(final String inputFile);

    /**
     * Save the metrics to a file
     */
    void saveMetrics();
}
