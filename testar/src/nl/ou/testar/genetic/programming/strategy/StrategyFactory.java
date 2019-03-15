package nl.ou.testar.genetic.programming.strategy;

import org.fruit.monkey.Settings;

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
     * Reset the metrics
     */
    void clear();

    /**
     * Write the metrics to a file
     *
     * @param settings - Current settings
     */
    void writeMetricsToFile(final Settings settings);

    /**
     * Save the metrics of a sequence
     */
    void saveMetrics();
}
