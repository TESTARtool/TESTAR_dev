/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */
package org.testar.webstudio.workspace;

import org.testar.core.CodingManager;
import org.testar.core.StateManagementTags;
import org.testar.core.tag.Tag;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class AbstractStateTagCatalog {

    private static final Set<Tag<?>> COMMON_TAGS = Set.of(
        StateManagementTags.WidgetControlType,
        StateManagementTags.WidgetTitle,
        StateManagementTags.WidgetIsEnabled,
        StateManagementTags.WidgetPath,
        StateManagementTags.WidgetHelpText,
        StateManagementTags.WidgetAutomationId,
        StateManagementTags.WidgetClassName,
        StateManagementTags.WidgetFrameworkId,
        StateManagementTags.WidgetBoundary,
        StateManagementTags.WidgetIsContentElement,
        StateManagementTags.WidgetIsControlElement
    );

    private AbstractStateTagCatalog() {
    }

    public record TagOption(String key, String label, String group, boolean defaultSelected) {
    }

    public static List<TagOption> options() {
        List<Tag<?>> defaults = Arrays.asList(CodingManager.getDefaultAbstractStateTags());
        return StateManagementTags.getAllTags().stream()
            .filter(tag -> StateManagementTags.getSettingsStringFromTag(tag) != null)
            .filter(tag -> StateManagementTags.getTagGroup(tag) != StateManagementTags.Group.ControlPattern)
            .map(tag -> new TagOption(
                StateManagementTags.getSettingsStringFromTag(tag),
                tag.name(),
                displayGroup(tag),
                defaults.contains(tag)
            ))
            .sorted(Comparator.comparing(TagOption::group).thenComparing(TagOption::key))
            .toList();
    }

    private static String displayGroup(Tag<?> tag) {
        if (COMMON_TAGS.contains(tag)) {
            return "Common";
        }
        if (StateManagementTags.getTagGroup(tag) == StateManagementTags.Group.General) {
            return "Windows";
        }
        return StateManagementTags.getTagGroup(tag).name();
    }
}
