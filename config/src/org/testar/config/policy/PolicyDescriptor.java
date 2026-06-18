/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.config.policy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.testar.core.Assert;

public final class PolicyDescriptor {

    private final Optional<String> policiesResourcePath;
    private final boolean replaceClickablePolicies;
    private final boolean replaceTypeablePolicies;
    private final boolean replaceScrollablePolicies;
    private final boolean replaceSelectablePolicies;
    private final boolean replaceEnabledPolicies;
    private final boolean replaceBlockedPolicies;
    private final boolean replaceWidgetFilterPolicies;
    private final boolean replaceVisiblePolicies;
    private final boolean replaceTopLevelPolicies;
    private final List<String> clickablePolicies;
    private final List<String> typeablePolicies;
    private final List<String> scrollablePolicies;
    private final List<String> selectablePolicies;
    private final List<String> enabledPolicies;
    private final List<String> blockedPolicies;
    private final List<String> widgetFilterPolicies;
    private final List<String> visiblePolicies;
    private final List<String> topLevelPolicies;

    public PolicyDescriptor(Optional<String> policiesResourcePath,
                            boolean replaceClickablePolicies,
                            boolean replaceTypeablePolicies,
                            boolean replaceScrollablePolicies,
                            boolean replaceSelectablePolicies,
                            boolean replaceEnabledPolicies,
                            boolean replaceBlockedPolicies,
                            boolean replaceWidgetFilterPolicies,
                            boolean replaceVisiblePolicies,
                            boolean replaceTopLevelPolicies,
                            List<String> clickablePolicies,
                            List<String> typeablePolicies,
                            List<String> scrollablePolicies,
                            List<String> selectablePolicies,
                            List<String> enabledPolicies,
                            List<String> blockedPolicies,
                            List<String> widgetFilterPolicies,
                            List<String> visiblePolicies,
                            List<String> topLevelPolicies) {
        this.policiesResourcePath = Assert.notNull(policiesResourcePath);
        this.replaceClickablePolicies = replaceClickablePolicies;
        this.replaceTypeablePolicies = replaceTypeablePolicies;
        this.replaceScrollablePolicies = replaceScrollablePolicies;
        this.replaceSelectablePolicies = replaceSelectablePolicies;
        this.replaceEnabledPolicies = replaceEnabledPolicies;
        this.replaceBlockedPolicies = replaceBlockedPolicies;
        this.replaceWidgetFilterPolicies = replaceWidgetFilterPolicies;
        this.replaceVisiblePolicies = replaceVisiblePolicies;
        this.replaceTopLevelPolicies = replaceTopLevelPolicies;
        this.clickablePolicies = immutableList(clickablePolicies);
        this.typeablePolicies = immutableList(typeablePolicies);
        this.scrollablePolicies = immutableList(scrollablePolicies);
        this.selectablePolicies = immutableList(selectablePolicies);
        this.enabledPolicies = immutableList(enabledPolicies);
        this.blockedPolicies = immutableList(blockedPolicies);
        this.widgetFilterPolicies = immutableList(widgetFilterPolicies);
        this.visiblePolicies = immutableList(visiblePolicies);
        this.topLevelPolicies = immutableList(topLevelPolicies);
    }

    public Optional<String> policiesResourcePath() {
        return policiesResourcePath;
    }

    public boolean replaceClickablePolicies() {
        return replaceClickablePolicies;
    }

    public boolean replaceTypeablePolicies() {
        return replaceTypeablePolicies;
    }

    public boolean replaceScrollablePolicies() {
        return replaceScrollablePolicies;
    }

    public boolean replaceSelectablePolicies() {
        return replaceSelectablePolicies;
    }

    public boolean replaceEnabledPolicies() {
        return replaceEnabledPolicies;
    }

    public boolean replaceBlockedPolicies() {
        return replaceBlockedPolicies;
    }

    public boolean replaceWidgetFilterPolicies() {
        return replaceWidgetFilterPolicies;
    }

    public boolean replaceVisiblePolicies() {
        return replaceVisiblePolicies;
    }

    public boolean replaceTopLevelPolicies() {
        return replaceTopLevelPolicies;
    }

    public List<String> clickablePolicies() {
        return clickablePolicies;
    }

    public List<String> typeablePolicies() {
        return typeablePolicies;
    }

    public List<String> scrollablePolicies() {
        return scrollablePolicies;
    }

    public List<String> selectablePolicies() {
        return selectablePolicies;
    }

    public List<String> enabledPolicies() {
        return enabledPolicies;
    }

    public List<String> blockedPolicies() {
        return blockedPolicies;
    }

    public List<String> widgetFilterPolicies() {
        return widgetFilterPolicies;
    }

    public List<String> visiblePolicies() {
        return visiblePolicies;
    }

    public List<String> topLevelPolicies() {
        return topLevelPolicies;
    }

    private static List<String> immutableList(List<String> values) {
        return Collections.unmodifiableList(Assert.notNull(values));
    }
}
