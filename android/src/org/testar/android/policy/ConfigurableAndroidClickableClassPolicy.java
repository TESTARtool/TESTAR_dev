/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.policy;

import org.testar.android.tag.AndroidTags;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Configurable clickable policy based on Android className values.
 */
public final class ConfigurableAndroidClickableClassPolicy implements ClickablePolicy {

    private final Set<String> clickableClasses;

    public ConfigurableAndroidClickableClassPolicy(Collection<String> clickableClasses) {
        this.clickableClasses = normalizeClasses(clickableClasses);
    }

    @Override
    public boolean isClickable(Widget widget) {
        if (widget == null || clickableClasses.isEmpty()) {
            return false;
        }

        String className = widget.get(AndroidTags.AndroidClassName, "");
        if (className.isBlank()) {
            return false;
        }

        return clickableClasses.contains(className.trim());
    }

    private Set<String> normalizeClasses(Collection<String> configuredClasses) {
        Set<String> normalizedClasses = new HashSet<String>();
        if (configuredClasses == null) {
            return normalizedClasses;
        }

        for (String configuredClass : configuredClasses) {
            if (configuredClass == null) {
                continue;
            }

            String normalizedClass = configuredClass.trim();
            if (!normalizedClass.isEmpty()) {
                normalizedClasses.add(normalizedClass);
            }
        }

        return normalizedClasses;
    }
}
