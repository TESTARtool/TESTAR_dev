package org.testar.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

/**
 * This is the interface for oracles - modules that determine whether the
 * an error or problem has occurred in the SUT.
 */

public interface Oracle {

    /**
     * Initialize the Oracle
     */
    public abstract void initialize();

    /**
     * Request that the Oracle determine a verdict about the current state of the SUT.
     * This method would usually be called by the getVerdict method in the protocol.
     *
     * @param state
     * @return verdict
     */
    public abstract Verdict getVerdict(State state);
}
