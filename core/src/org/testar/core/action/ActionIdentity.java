/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.IdentityEncoding;

/** Stable, length-delimited identity data shared by individual and compound actions. */
public final class ActionIdentity {

    private ActionIdentity() { }

    public static String describe(State state, Action action, boolean abstractIdentity) {
        Tag<String> widgetIdTag = abstractIdentity ? Tags.AbstractID : Tags.ConcreteID;
        Widget originWidget = action.get(Tags.OriginWidget, null);
        String widgetId = originWidget == null ? null : originWidget.get(widgetIdTag);
        return encode(action.getClass().getName(), action.get(Tags.Role, ActionRoles.Action).name(),
                widgetId, action.getIdentityParameters(state, abstractIdentity));
    }

    public static String encode(String... fields) {
        return IdentityEncoding.encode(fields);
    }
}
