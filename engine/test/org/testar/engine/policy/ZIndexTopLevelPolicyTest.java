/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class ZIndexTopLevelPolicyTest {

    @Test
    public void returnsTrueWhenWidgetMatchesStateMaxZIndex() {
        ZIndexTopLevelPolicy policy = new ZIndexTopLevelPolicy();
        StateStub state = new StateStub();
        state.set(Tags.MaxZIndex, 10.0);

        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.set(Tags.ZIndex, 10.0);
        state.addChild(widget);

        Assert.assertTrue(policy.isTopLevel(widget));
    }

    @Test
    public void returnsFalseWhenWidgetDoesNotMatchStateMaxZIndex() {
        ZIndexTopLevelPolicy policy = new ZIndexTopLevelPolicy();
        StateStub state = new StateStub();
        state.set(Tags.MaxZIndex, 10.0);

        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.set(Tags.ZIndex, 5.0);
        state.addChild(widget);

        Assert.assertFalse(policy.isTopLevel(widget));
    }

    @Test
    public void returnsFalseWhenStateOrWidgetZIndexIsMissing() {
        ZIndexTopLevelPolicy policy = new ZIndexTopLevelPolicy();
        WidgetStub widget = new WidgetStub();

        Assert.assertFalse(policy.isTopLevel(widget));
    }
}
