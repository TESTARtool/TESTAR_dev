/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.testar.core.action.policy.TypeablePolicy;
import org.testar.core.alayer.Role;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverTypeablePolicy implements TypeablePolicy {

    private static final Set<Role> NATIVE_TYPEABLE_ROLES =
            new HashSet<>(Arrays.asList(WdRoles.nativeTypeableRoles()));
    private final Set<String> customTypeableClasses;

    public WebdriverTypeablePolicy() {
        this(Collections.emptyList());
    }

    public WebdriverTypeablePolicy(Collection<String> customTypeableClasses) {
        this.customTypeableClasses = normalizeClasses(customTypeableClasses);
    }

    @Override
    public boolean isTypeable(Widget widget) {
        if (widget == null || !widget.get(WdTags.WebIsEnabled, Boolean.TRUE)) {
            return false;
        }

        Role role = widget.get(Tags.Role, null);
        if (role == null || !NATIVE_TYPEABLE_ROLES.contains(role)) {
            return false;
        }
        if (matchesCustomTypeableClass(widget)) {
            return true;
        }

        if (!WdRoles.WdINPUT.equals(role)) {
            return true;
        }

        String inputType = widget.get(WdTags.WebType, "");
        return WdRoles.typeableInputTypes().contains(inputType.toLowerCase(Locale.ROOT));
    }

    private boolean matchesCustomTypeableClass(Widget widget) {
        if (customTypeableClasses.isEmpty()) {
            return false;
        }

        String cssClasses = widget.get(WdTags.WebCssClasses, "");
        if (cssClasses.isBlank()) {
            return false;
        }

        for (String cssClass : splitCssClasses(cssClasses)) {
            if (customTypeableClasses.contains(cssClass)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> normalizeClasses(Collection<String> cssClasses) {
        Set<String> normalizedClasses = new HashSet<>();
        if (cssClasses == null) {
            return normalizedClasses;
        }

        for (String cssClass : cssClasses) {
            if (cssClass == null) {
                continue;
            }

            String normalizedClass = cssClass.trim();
            if (!normalizedClass.isEmpty()) {
                normalizedClasses.add(normalizedClass);
            }
        }

        return normalizedClasses;
    }

    private List<String> splitCssClasses(String cssClasses) {
        List<String> splitClasses = new ArrayList<>();
        String normalizedCssClasses = cssClasses.replace('[', ' ').replace(']', ' ').replace(',', ' ');
        for (String cssClass : normalizedCssClasses.split("\\s+")) {
            String normalizedClass = cssClass.trim();
            if (!normalizedClass.isEmpty()) {
                splitClasses.add(normalizedClass);
            }
        }

        return splitClasses;
    }
}
