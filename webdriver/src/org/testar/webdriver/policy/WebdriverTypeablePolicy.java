/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.testar.core.alayer.Role;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverTypeablePolicy implements TypeablePolicy {

    private static final Set<Role> NATIVE_TYPEABLE_ROLES =
            new HashSet<>(Arrays.asList(WdRoles.nativeTypeableRoles()));

    @Override
    public boolean isTypeable(Widget widget) {
        if (widget == null) {
            return false;
        }

        Role role = widget.get(Tags.Role, null);
        if (role == null || !NATIVE_TYPEABLE_ROLES.contains(role)) {
            return false;
        }

        if (!WdRoles.WdINPUT.equals(role)) {
            return true;
        }

        String inputType = widget.get(WdTags.WebType, "");
        return WdRoles.typeableInputTypes().contains(inputType.toLowerCase(Locale.ROOT));
    }
}
