/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.util.Locale;

import org.testar.core.alayer.Roles;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.state.SemanticStateFormatter;
import org.testar.engine.state.SemanticWidgetDescriptor;

final class WindowsSemanticWidgetDescriptor implements SemanticWidgetDescriptor {

    @Override
    public boolean shouldInclude(Widget widget) {
        if (widget == null || widget == widget.root()) {
            return false;
        }

        return isMeaningfulContext(widget);
    }

    @Override
    public String roleOf(Widget widget) {
        return lower(String.valueOf(widget.get(Tags.Role, Roles.Widget)));
    }

    @Override
    public String labelOf(Widget widget) {
        String[] candidates = new String[] {
                widget.get(Tags.Title, ""),
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
        return SemanticStateFormatter.sanitize(widget.get(Tags.ValuePattern, ""));
    }

    private boolean isMeaningfulContext(Widget widget) {
        return !labelOf(widget).isBlank() || !valueOf(widget).isBlank();
    }

    private String lower(String value) {
        return SemanticStateFormatter.sanitize(value).toLowerCase(Locale.ROOT);
    }
}
