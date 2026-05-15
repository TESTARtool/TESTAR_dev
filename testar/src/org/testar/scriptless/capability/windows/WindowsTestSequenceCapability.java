/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.windows;

import org.testar.core.Assert;
import org.testar.core.environment.Environment;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.oracle.windows.log.ProcessListenerOracle;
import org.testar.config.ConfigTags;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSequenceCapability;

import java.util.List;

public class WindowsTestSequenceCapability extends TestSequenceCapability {

    private final TestSequenceCapability delegate;

    public WindowsTestSequenceCapability(TestSequenceCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void startTestSequence(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        delegate.startTestSequence(runtimeContext);
    }

    @Override
    public void beginSequence(RuntimeContext runtimeContext, SUT system, State initialState) {
        Assert.notNull(runtimeContext, system, initialState);
        delegate.beginSequence(runtimeContext, system, initialState);

        if (runtimeContext.settings().get(ConfigTags.ProcessListenerEnabled, false)) {
            Oracle processListenerOracle = new ProcessListenerOracle(system, runtimeContext.settings());
            processListenerOracle.initialize();
            runtimeContext.setProcessListenerOracle(processListenerOracle);
        }

        double displayScale = Environment.getInstance().getDisplayScale(initialState.child(0).get(Tags.HWND, (long)0));
        runtimeContext.mouse().setCursorDisplayScale(displayScale);
    }

    @Override
    public void finishTestSequence(RuntimeContext runtimeContext, List<Verdict> verdicts) {
        Assert.notNull(runtimeContext, verdicts);
        delegate.finishTestSequence(runtimeContext, verdicts);
    }
}
