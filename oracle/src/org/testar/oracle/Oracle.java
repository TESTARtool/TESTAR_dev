/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle;

import java.util.List;

import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;

/**
 * This is the interface for oracles - modules that determine whether the
 * an error or problem has occurred in the SUT.
 */

public interface Oracle extends OracleWidgetReporter, OracleApplicationStatus {

    /**
     * Initialize the Oracle
     */
    default void initialize() {
        // Nothing to initialize by default
    }

    /**
     * The message that will be shown to the user when reporting a Verdict. 
     * 
     * @return
     */
    default String getMessage() {
        // No message to report by default
        return "";
    }

    /**
     * Request that the Oracle determine verdicts about the current state of the SUT.
     * This method would usually be called by the getVerdicts method in the protocol.
     *
     * @param state
     * @return list of verdicts
     */
    public abstract List<Verdict> getVerdicts(State state);

}
