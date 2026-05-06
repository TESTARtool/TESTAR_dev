/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.Role;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

/**
 * Default Android typeable-widget policy based on native Android roles and
 * Appium state attributes.
 */
public final class AndroidTypeablePolicy implements TypeablePolicy {

    private static final Set<Role> NATIVE_TYPEABLE_ROLES =
            new HashSet<>(Arrays.asList(AndroidRoles.nativeTypeableRoles()));

    @Override
    public boolean isTypeable(Widget widget) {
        if (widget == null) {
            return false;
        }

        Role role = widget.get(Tags.Role, null);
        if (role == null || !NATIVE_TYPEABLE_ROLES.contains(role)) {
            return false;
        }

        return widget.get(AndroidTags.AndroidEnabled, false)
                && widget.get(AndroidTags.AndroidFocusable, false)
                && widget.get(AndroidTags.AndroidDisplayed, false);
    }
}
