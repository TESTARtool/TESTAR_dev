/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.state;

import java.util.Set;

import org.testar.core.alayer.Roles;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.state.SemanticStateFormatter;
import org.testar.engine.state.SemanticWidgetDescriptor;
import org.testar.webdriver.tag.WdTags;

final class WebdriverSemanticWidgetDescriptor implements SemanticWidgetDescriptor {

    private static final Set<String> WEB_UNMEANINGFUL_TAGS = Set.of(
            "div",
            "path",
            "svg"
    );

    @Override
    public boolean shouldInclude(Widget widget) {
        if (widget == null || widget == widget.root()) {
            return false;
        }
        if (!isVisible(widget)) {
            return false;
        }

        return isMeaningfulContext(widget);
    }

    @Override
    public String roleOf(Widget widget) {
        String ariaRole = lower(widget.get(WdTags.WebAriaRole, ""));
        if (!ariaRole.isBlank()) {
            return ariaRole;
        }

        String tagName = lower(widget.get(WdTags.WebTagName, ""));
        if ("input".equals(tagName)) {
            String inputType = lower(widget.get(WdTags.WebType, ""));
            if (!inputType.isBlank()) {
                return "input:" + inputType;
            }
        }
        if (!tagName.isBlank()) {
            return tagName;
        }

        return lower(String.valueOf(widget.get(Tags.Role, Roles.Widget)));
    }

    @Override
    public String labelOf(Widget widget) {
        String[] candidates = new String[] {
                widget.get(WdTags.WebAriaLabel, ""),
                widget.get(WdTags.WebAriaLabelledBy, ""),
                widget.get(WdTags.WebPlaceholder, ""),
                widget.get(WdTags.WebInnerText, ""),
                widget.get(Tags.Title, ""),
                widget.get(WdTags.WebTitle, ""),
                widget.get(WdTags.WebName, ""),
                widget.get(Tags.Desc, "")
        };

        for (String candidate : candidates) {
            String normalized = SemanticStateFormatter.sanitize(candidate);
            if (!normalized.isBlank()) {
                return normalized;
            }
        }
        return "";
    }

    @Override
    public String valueOf(Widget widget) {
        String[] candidates = new String[] {
                widget.get(WdTags.WebValue, ""),
                widget.get(Tags.ValuePattern, "")
        };

        for (String candidate : candidates) {
            String normalized = SemanticStateFormatter.sanitize(candidate);
            if (!normalized.isBlank()) {
                return normalized;
            }
        }
        return "";
    }

    private boolean isMeaningfulContext(Widget widget) {
        if (labelOf(widget).isBlank() && valueOf(widget).isBlank()) {
            return false;
        }
        if (isIgnoredUnmeaningful(widget)) {
            return false;
        }
        if (isShadowedByMeaningfulAncestor(widget)) {
            return false;
        }
        return true;
    }

    private boolean isIgnoredUnmeaningful(Widget widget) {
        String tagName = lower(widget.get(WdTags.WebTagName, ""));
        return WEB_UNMEANINGFUL_TAGS.contains(tagName);
    }

    private boolean isShadowedByMeaningfulAncestor(Widget widget) {
        String widgetLabel = labelOf(widget);
        if (widgetLabel.isBlank()) {
            return false;
        }

        Widget parent = widget.parent();
        while (parent != null && parent != parent.root()) {
            if (!isVisible(parent)) {
                parent = parent.parent();
                continue;
            }

            String parentLabel = labelOf(parent);
            if (!parentLabel.isBlank()
                    && widgetLabel.equals(parentLabel)
                    && !isIgnoredUnmeaningful(parent)) {
                return true;
            }
            parent = parent.parent();
        }
        return false;
    }

    private boolean isVisible(Widget widget) {
        if (widget.get(WdTags.WebAriaHidden, false)) {
            return false;
        }
        if (widget.get(WdTags.WebIsHidden, false)) {
            return false;
        }
        if (!widget.get(WdTags.WebIsDisplayed, true)) {
            return false;
        }
        return widget.get(WdTags.WebIsActuallyVisible, true);
    }

    private String lower(String value) {
        return SemanticStateFormatter.sanitize(value).toLowerCase();
    }
}
