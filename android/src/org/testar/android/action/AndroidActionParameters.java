/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

import org.testar.android.tag.AndroidTags;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

final class AndroidActionParameters {

    private AndroidActionParameters() {
    }

    static String forWidget(Action action, Widget widget, String operation) {
        return "role=" + action.get(Tags.Role)
                + ",action=" + operation
                + ",widget=" + widget.get(Tags.ConcreteID, "NoWidgetConcreteIdAvailable")
                + ",widgetClass=" + widget.get(AndroidTags.AndroidClassName, "")
                + ",text=" + widget.get(AndroidTags.AndroidText, "")
                + ",accessibilityId=" + widget.get(AndroidTags.AndroidAccessibilityId, "")
                + ",xpath=" + widget.get(AndroidTags.AndroidXpath, "");
    }

    static String forSystem(Action action, String operation) {
        State state = (State) action.get(Tags.OriginWidget);
        return "role=" + action.get(Tags.Role)
                + ",state=" + state.get(Tags.ConcreteID, "NoStateConcreteIdAvailable")
                + ",action=" + operation;
    }
}
