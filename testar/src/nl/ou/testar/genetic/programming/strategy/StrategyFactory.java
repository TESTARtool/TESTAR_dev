package nl.ou.testar.genetic.programming.strategy;

import org.fruit.alayer.Verdict;
import org.fruit.monkey.Settings;

public interface StrategyFactory {

    /**
     * Get the strategy action selector
     *
     * @return StrategyActionSelector
     */
    StrategyActionSelector getStrategyActionSelector();

    /**
     * Returns inputs for
     *
     * @param inputFile - File to read inputs from. If a line starts with a # it is considered a comment
     * @return inputs for type actions
     */
    String[] getTextInputsFromFile(final String inputFile);

    /**
     * Keep track of current sequence and start time
     */
    void prepareForSequence();

    /**
     * Execute post sequence actions like setting stop time and print the time it took to execute a sequence
     */
    void postSequence(final Settings settings, final Verdict verdict);
}
