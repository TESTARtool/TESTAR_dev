/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.state;

import java.util.Locale;

import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.Roles;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.state.SemanticStateFormatter;
import org.testar.engine.state.SemanticWidgetDescriptor;

final class AndroidSemanticWidgetDescriptor implements SemanticWidgetDescriptor {

    @Override
    public boolean shouldInclude(Widget widget) {
        if (widget == null || widget == widget.root()) {
            return false;
        }
        if (!widget.get(AndroidTags.AndroidDisplayed, true)) {
            return false;
        }

        return !labelOf(widget).isBlank() || !valueOf(widget).isBlank();
    }

    @Override
    public String roleOf(Widget widget) {
        String className = sanitize(widget.get(AndroidTags.AndroidClassName, ""));
        if (!className.isBlank()) {
            return className.toLowerCase(Locale.ROOT);
        }
        return sanitize(String.valueOf(widget.get(Tags.Role, Roles.Widget))).toLowerCase(Locale.ROOT);
    }

    @Override
    public String labelOf(Widget widget) {
        String[] candidates = new String[] {
                widget.get(AndroidTags.AndroidText, ""),
                widget.get(AndroidTags.AndroidHint, ""),
                widget.get(Tags.Title, "")
        };

        for (String candidate : candidates) {
            String normalized = sanitize(candidate);
            if (!normalized.isBlank()) {
                return normalized;
            }
        }
        return "";
    }

    @Override
    public String valueOf(Widget widget) {
        String[] candidates = new String[] {
                widget.get(AndroidTags.AndroidResourceId, ""),
                widget.get(AndroidTags.AndroidAccessibilityId, "")
        };

        for (String candidate : candidates) {
            String normalized = sanitize(candidate);
            if (!normalized.isBlank()) {
                return normalized;
            }
        }
        return "";
    }

    private String sanitize(String value) {
        return SemanticStateFormatter.sanitize(value);
    }
}
