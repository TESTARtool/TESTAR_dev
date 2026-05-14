/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.windows;

import java.util.List;

import org.testar.core.Assert;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.service.ScriptlessOracleComposer;

public class ScriptlessWindowsOracleComposer extends ScriptlessOracleComposer {

    private final ScriptlessOracleComposer delegate;

    public ScriptlessWindowsOracleComposer(ScriptlessOracleComposer delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public List<Verdict> composeVerdicts(RuntimeContext runtimeContext,
                                         SUT system,
                                         State state,
                                         List<Verdict> verdicts) {
        Assert.notNull(runtimeContext);
        Assert.notNull(system, state, verdicts);
        return delegate.composeVerdicts(runtimeContext, system, state, verdicts);
    }
}
