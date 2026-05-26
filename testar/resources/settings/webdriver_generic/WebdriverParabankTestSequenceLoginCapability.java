/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import java.util.List;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSequenceCapability;

import static org.testar.scriptless.util.TriggerActionUtil.clickMatchingWidget;
import static org.testar.scriptless.util.TriggerActionUtil.typeMatchingWidget;
import static org.testar.scriptless.util.TriggerActionUtil.pasteMatchingWidget;

public final class WebdriverParabankTestSequenceLoginCapability extends TestSequenceCapability {

    private final TestSequenceCapability delegate;

    public WebdriverParabankTestSequenceLoginCapability(TestSequenceCapability delegate) {
        this.delegate = delegate;
    }

    @Override
    public void startTestSequence(RuntimeContext runtimeContext) {
        delegate.startTestSequence(runtimeContext);
    }

    @Override
    public void beginSequence(RuntimeContext runtimeContext, SUT system, State initialState) {
        delegate.beginSequence(runtimeContext, system, initialState);

        // Login Parabank
        typeMatchingWidget("name", "username", "john", initialState, system, runtimeContext, 5, 1.0);
        pasteMatchingWidget("name", "password", "demo", initialState, system, runtimeContext, 5, 1.0);
        clickMatchingWidget("value", "Log In", initialState, system, runtimeContext, 5, 1.0);
    }

    @Override
    public void finishTestSequence(RuntimeContext runtimeContext, List<Verdict> verdicts) {
        delegate.finishTestSequence(runtimeContext, verdicts);
    }
}
