/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.policy;

import org.testar.core.alayer.Role;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.windows.alayer.UIARoles;
import org.testar.windows.tag.UIATags;

/**
 * Default Windows/UIA typeable-widget policy.
 */
public final class WindowsTypeablePolicy implements TypeablePolicy {

    @Override
    public boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, null);
        if (role == null) {
            return false;
        }
        if (!role.isA(UIARoles.UIAEdit) && !role.isA(UIARoles.UIADocument)) {
            return false;
        }
        if (!widget.get(UIATags.UIAIsKeyboardFocusable, false)) {
            return false;
        }
        return widget.get(UIATags.UIAIsValuePatternAvailable, false)
                && !widget.get(UIATags.UIAValueIsReadOnly, false);
    }
}
