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
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverClickablePolicy implements ClickablePolicy {

    private static final Set<Role> NATIVE_CLICKABLE_ROLES =
            new HashSet<>(Arrays.asList(WdRoles.nativeClickableRoles()));

    private static final Set<String> ARIA_CLICKABLE_ROLES =
            new HashSet<>(WdRoles.ariaClickableRoles());

    @Override
    public boolean isClickable(Widget widget) {
        if (widget == null) {
            return false;
        }
        if (widget.get(WdTags.WebIsClickable, Boolean.FALSE)) {
            return true;
        }

        String ariaRole = widget.get(WdTags.WebAriaRole, "");
        if (!ariaRole.isEmpty() && ARIA_CLICKABLE_ROLES.contains(ariaRole)) {
            return true;
        }

        Role nativeRole = widget.get(Tags.Role, null);
        if (nativeRole == null || !NATIVE_CLICKABLE_ROLES.contains(nativeRole)) {
            return false;
        }

        String inputType = widget.get(WdTags.WebType, "");
        if (!WdRoles.WdINPUT.equals(nativeRole)) {
            return true;
        }
        return WdRoles.clickableInputTypes().contains(inputType.toLowerCase(Locale.ROOT));
    }
}
