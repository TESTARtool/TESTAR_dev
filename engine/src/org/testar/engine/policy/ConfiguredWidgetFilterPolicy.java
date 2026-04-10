/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.util.Util;

public final class ConfiguredWidgetFilterPolicy implements WidgetFilterPolicy {

    private final List<String> tagsToFilter;
    private final Pattern clickFilterPattern;

    public ConfiguredWidgetFilterPolicy(Settings settings) {
        this(
                settings.get(ConfigTags.TagsToFilter, Collections.emptyList()),
                settings.get(ConfigTags.ClickFilter, "(?!x)x")
        );
    }

    public ConfiguredWidgetFilterPolicy(List<String> tagsToFilter, String clickFilterRegex) {
        this.tagsToFilter = Collections.unmodifiableList(Assert.notNull(tagsToFilter));
        this.clickFilterPattern = Pattern.compile(Assert.notNull(clickFilterRegex), Pattern.UNICODE_CHARACTER_CLASS);
    }

    @Override
    public boolean allows(Widget widget) {
        if (!Util.hitTest(widget, 0.5, 0.5)) {
            return false;
        }

        for (String tagName : tagsToFilter) {
            String tagValue = findTagValue(widget, tagName);
            if (tagValue == null || tagValue.isEmpty()) {
                continue;
            }
            if (clickFilterPattern.matcher(tagValue).matches()) {
                return false;
            }
        }

        return true;
    }

    private static String findTagValue(Widget widget, String tagName) {
        for (Tag<?> tag : widget.tags()) {
            if (!tag.name().equals(tagName)) {
                continue;
            }

            Object value = widget.get(tag, null);
            if (value != null) {
                return value.toString();
            }
        }

        return null;
    }
}
