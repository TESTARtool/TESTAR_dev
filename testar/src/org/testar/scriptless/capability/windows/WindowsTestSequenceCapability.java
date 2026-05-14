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
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSequenceCapability;

public class WindowsTestSequenceCapability extends TestSequenceCapability {

    private final TestSequenceCapability delegate;

    public WindowsTestSequenceCapability(TestSequenceCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void beginSequence(RuntimeContext runtimeContext, SUT system, State initialState) {
        Assert.notNull(runtimeContext, system, initialState);
        delegate.beginSequence(runtimeContext, system, initialState);

        double displayScale = Environment.getInstance().getDisplayScale(initialState.child(0).get(Tags.HWND, (long)0));
        runtimeContext.mouse().setCursorDisplayScale(displayScale);
    }
}
