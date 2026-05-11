/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action;

import org.testar.android.tag.AndroidTags;
import org.testar.core.state.Widget;

final class AndroidActionDescriptions {

    private AndroidActionDescriptions() {
    }

    static String describeSystemAction(String actionType) {
        return "Android " + actionType;
    }

    static String describeWidgetAction(String actionType, Widget widget) {
        return "Android " + actionType + " on widget with " + describeWidgetTarget(widget);
    }

    static String describeWidgetActionWithInput(String actionType, Widget widget, String inputText) {
        return "Android " + actionType
                + " on widget with " + describeTextInputTarget(widget)
                + " typing text: " + inputText;
    }

    private static String describeWidgetTarget(Widget widget) {
        return describeWidgetTarget(widget, false);
    }

    private static String describeTextInputTarget(Widget widget) {
        return describeWidgetTarget(widget, true);
    }

    private static String describeWidgetTarget(Widget widget, boolean preferHintOverText) {
        if (widget == null) {
            return "unknown widget";
        }

        String accessibilityId = trim(widget.get(AndroidTags.AndroidAccessibilityId, ""));
        if (!accessibilityId.isEmpty()) {
            return "content-desc '" + accessibilityId + "'";
        }

        String hint = trim(widget.get(AndroidTags.AndroidHint, ""));
        String text = trim(widget.get(AndroidTags.AndroidText, ""));

        // For typing actions: content-desc > hint > text > resource-id > class > xpath
        if (preferHintOverText) {
            if (!hint.isEmpty()) {
                return "hint '" + hint + "'";
            }
            if (!text.isEmpty()) {
                return "text '" + text + "'";
            }
        } 
        // Otherwise: content-desc > text > hint > resource-id > class > xpath
        else {
            if (!text.isEmpty()) {
                return "text '" + text + "'";
            }
            if (!hint.isEmpty()) {
                return "hint '" + hint + "'";
            }
        }

        String resourceId = trim(widget.get(AndroidTags.AndroidResourceId, ""));
        if (!resourceId.isEmpty()) {
            return "resource-id '" + resourceId + "'";
        }

        String className = trim(widget.get(AndroidTags.AndroidClassName, ""));
        if (!className.isEmpty()) {
            return "type '" + className + "'";
        }

        String xpath = trim(widget.get(AndroidTags.AndroidXpath, ""));
        if (!xpath.isEmpty()) {
            return "xpath '" + xpath + "'";
        }

        return "unknown widget";
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
