/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.alayer.Rect;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.stub.WidgetStub;

public final class ConfiguredWidgetFilterPolicyTest {

    private static final Tag<String> FILTER_TAG = Tag.from("FilterTag", String.class);

    @Test
    public void rejectsWidgetWhenHitTestFails() {
        ConfiguredWidgetFilterPolicy policy = new ConfiguredWidgetFilterPolicy(List.of("FilterTag"), ".*blocked.*");
        Widget widget = widgetWithHitTester(Util.FalseTester);
        widget.set(FILTER_TAG, "allowed");

        Assert.assertFalse(policy.allows(widget));
    }

    @Test
    public void rejectsWidgetWhenConfiguredTagMatchesPattern() {
        ConfiguredWidgetFilterPolicy policy = new ConfiguredWidgetFilterPolicy(List.of("FilterTag"), ".*blocked.*");
        Widget widget = widgetWithHitTester(Util.TrueTester);
        widget.set(FILTER_TAG, "blocked button");

        Assert.assertFalse(policy.allows(widget));
    }

    @Test
    public void allowsWidgetWhenConfiguredTagsDoNotMatchPattern() {
        ConfiguredWidgetFilterPolicy policy = new ConfiguredWidgetFilterPolicy(List.of("FilterTag"), ".*blocked.*");
        Widget widget = widgetWithHitTester(Util.TrueTester);
        widget.set(FILTER_TAG, "allowed button");

        Assert.assertTrue(policy.allows(widget));
    }

    @Test
    public void allowsWidgetWhenConfiguredTagIsMissing() {
        ConfiguredWidgetFilterPolicy policy = new ConfiguredWidgetFilterPolicy(List.of("FilterTag"), ".*blocked.*");
        Widget widget = widgetWithHitTester(Util.TrueTester);

        Assert.assertTrue(policy.allows(widget));
    }

    private static Widget widgetWithHitTester(org.testar.core.alayer.HitTester hitTester) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Shape, Rect.from(0, 0, 100, 40));
        widget.set(Tags.HitTester, hitTester);
        return widget;
    }
}
