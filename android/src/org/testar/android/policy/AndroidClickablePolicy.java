/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.Role;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

/**
 * Default Android clickable-widget policy based on native Android roles and
 * Appium state attributes.
 */
public final class AndroidClickablePolicy implements ClickablePolicy {

    private static final Set<Role> NATIVE_CLICKABLE_ROLES =
            new HashSet<>(Arrays.asList(AndroidRoles.nativeClickableRoles()));

    @Override
    public boolean isClickable(Widget widget) {
        if (widget == null) {
            return false;
        }

        Role role = widget.get(Tags.Role, null);
        if (role == null || !NATIVE_CLICKABLE_ROLES.contains(role)) {
            return false;
        }

        return widget.get(AndroidTags.AndroidClickable, true)
                && widget.get(AndroidTags.AndroidEnabled, true)
                && widget.get(AndroidTags.AndroidDisplayed, true);
    }
}
