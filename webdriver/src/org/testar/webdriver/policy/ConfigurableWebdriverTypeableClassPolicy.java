/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;
import org.testar.webdriver.tag.WdTags;

/**
 * Configurable typeable policy based on WebDriver CSS class values.
 */
public final class ConfigurableWebdriverTypeableClassPolicy implements TypeablePolicy {

    private final Set<String> typeableClasses;

    public ConfigurableWebdriverTypeableClassPolicy(Collection<String> typeableClasses) {
        this.typeableClasses = normalizeClasses(typeableClasses);
    }

    @Override
    public boolean isTypeable(Widget widget) {
        if (widget == null || typeableClasses.isEmpty()) {
            return false;
        }

        String cssClasses = widget.get(WdTags.WebCssClasses, "");
        if (cssClasses.isBlank()) {
            return false;
        }

        for (String cssClass : splitCssClasses(cssClasses)) {
            if (typeableClasses.contains(cssClass)) {
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
