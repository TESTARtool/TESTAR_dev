/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import java.util.Locale;

import org.testar.core.Assert;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class SemanticStateFormatter {

    private SemanticStateFormatter() {
    }

    static String describe(Widget widget, SemanticWidgetDescriptor descriptor) {
        Assert.notNull(widget);
        Assert.notNull(descriptor);

        String role = sanitize(descriptor.roleOf(widget)).toLowerCase(Locale.ROOT);
        String label = sanitize(descriptor.labelOf(widget));
        String value = sanitize(descriptor.valueOf(widget));
        boolean enabled = widget.get(Tags.Enabled, true);
        boolean blocked = widget.get(Tags.Blocked, false);

        StringBuilder builder = new StringBuilder();
        builder.append("role=").append(role);
        if (!label.isBlank()) {
            builder.append(";label=").append(label);
        }
        if (!value.isBlank()) {
            builder.append(";value=").append(value);
        }
        //builder.append(";enabled=").append(enabled);
        //builder.append(";blocked=").append(blocked);
        return builder.toString();
    }

    public static String sanitize(String value) {
        String normalized = value == null ? "" : value.trim();
        normalized = normalized.replace('\r', ' ');
        normalized = normalized.replace('\n', ' ');
        normalized = normalized.replace(';', ',');
        normalized = normalized.replace('[', '(');
        normalized = normalized.replace(']', ')');
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized;
    }
}
