/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.tag.AndroidTags;
import org.testar.core.action.Action;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.engine.action.resolver.DescriptionActionResolver;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class AndroidTypeActionResolutionTest {

    @Test
    public void testResolvedAndroidTypeActionPreservesIdentityTags() {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
        widget.set(AndroidTags.AndroidHint, "Enter your username");
        widget.set(AndroidTags.AndroidClassName, "android.widget.EditText");
        widget.set(AndroidTags.AndroidXpath, "/hierarchy/android.widget.EditText");

        Action templateAction = new AndroidActionType(state, widget, "RandomInput");
        templateAction.set(Tags.AbstractID, "AAtype123");
        templateAction.set(Tags.ConcreteID, "CCtype123");

        DescriptionActionResolver resolver = new DescriptionActionResolver();
        ResolvedAction resolvedAction = resolver.resolve(
                Collections.singleton(templateAction),
                List.of("type", "Enter your username", "testar")
        );

        Assert.assertTrue(resolvedAction.action() instanceof AndroidActionType);
        Assert.assertEquals("AAtype123", resolvedAction.action().get(Tags.AbstractID, ""));
        Assert.assertEquals("CCtype123", resolvedAction.action().get(Tags.ConcreteID, ""));
        Assert.assertEquals("testar", resolvedAction.action().get(Tags.InputText, ""));
    }
}
