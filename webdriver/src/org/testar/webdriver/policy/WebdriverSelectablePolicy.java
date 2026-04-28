/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testar.core.alayer.Role;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.alayer.WdRoles;

public final class WebdriverSelectablePolicy implements SelectablePolicy {

    private static final Set<Role> NATIVE_SELECTABLE_ROLES =
            new HashSet<>(Arrays.asList(WdRoles.nativeSelectableRoles()));

    @Override
    public boolean isSelectable(Widget widget) {
        if (widget == null) {
            return false;
        }

        Role nativeRole = widget.get(Tags.Role, null);
        if (nativeRole == null || !NATIVE_SELECTABLE_ROLES.contains(nativeRole)) {
            return false;
        }

        return true;
    }
}
