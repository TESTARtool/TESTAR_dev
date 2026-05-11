/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.policy;

import org.testar.android.tag.AndroidTags;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Configurable typeable policy based on Android className values.
 */
public final class ConfigurableAndroidTypeableClassPolicy implements TypeablePolicy {

    private final Set<String> typeableClasses;

    public ConfigurableAndroidTypeableClassPolicy(Collection<String> typeableClasses) {
        this.typeableClasses = normalizeClasses(typeableClasses);
    }

    @Override
    public boolean isTypeable(Widget widget) {
        if (widget == null || typeableClasses.isEmpty()) {
            return false;
        }

        String className = widget.get(AndroidTags.AndroidClassName, "");
        if (className.isBlank()) {
            return false;
        }

        return typeableClasses.contains(className.trim());
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
