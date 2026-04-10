/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import org.testar.core.Assert;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.WidgetFilterPolicy;

/**
 * Groups action-derivation policies so both CLI and TESTAR can use the same
 * runtime logic with different custom policy compositions.
 */
public final class ActionDerivationContext {

    private final ClickablePolicy clickablePolicy;
    private final TypeablePolicy typeablePolicy;
    private final ScrollablePolicy scrollablePolicy;
    private final EnabledPolicy enabledPolicy;
    private final BlockedPolicy blockedPolicy;
    private final WidgetFilterPolicy widgetFilterPolicy;

    public ActionDerivationContext(ClickablePolicy clickablePolicy,
                                   TypeablePolicy typeablePolicy,
                                   ScrollablePolicy scrollablePolicy,
                                   EnabledPolicy enabledPolicy,
                                   BlockedPolicy blockedPolicy,
                                   WidgetFilterPolicy widgetFilterPolicy) {
        this.clickablePolicy = Assert.notNull(clickablePolicy);
        this.typeablePolicy = Assert.notNull(typeablePolicy);
        this.scrollablePolicy = Assert.notNull(scrollablePolicy);
        this.enabledPolicy = Assert.notNull(enabledPolicy);
        this.blockedPolicy = Assert.notNull(blockedPolicy);
        this.widgetFilterPolicy = Assert.notNull(widgetFilterPolicy);
    }

    public ClickablePolicy clickablePolicy() {
        return clickablePolicy;
    }

    public TypeablePolicy typeablePolicy() {
        return typeablePolicy;
    }

    public ScrollablePolicy scrollablePolicy() {
        return scrollablePolicy;
    }

    public EnabledPolicy enabledPolicy() {
        return enabledPolicy;
    }

    public BlockedPolicy blockedPolicy() {
        return blockedPolicy;
    }

    public WidgetFilterPolicy widgetFilterPolicy() {
        return widgetFilterPolicy;
    }
}
