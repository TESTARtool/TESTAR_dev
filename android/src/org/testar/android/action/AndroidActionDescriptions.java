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
        return "Android " + actionType + " on widget " + describeWidgetTarget(widget);
    }

    static String describeWidgetActionWithInput(String actionType, Widget widget, String inputText) {
        return "Android " + actionType + " text " + inputText
                + " on widget " + describeTextInputTarget(widget);
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

        String role = semanticRole(widget);
        String accessibilityId = trim(widget.get(AndroidTags.AndroidAccessibilityId, ""));
        if (!accessibilityId.isEmpty()) {
            return roleSemantic(role, accessibilityId);
        }

        String hint = trim(widget.get(AndroidTags.AndroidHint, ""));
        String text = trim(widget.get(AndroidTags.AndroidText, ""));

        // For typing actions: content-desc > hint > text > resource-id > class > xpath
        if (preferHintOverText) {
            if (!hint.isEmpty()) {
                return roleSemantic(role, hint);
            }
            if (!text.isEmpty()) {
                return roleSemantic(role, text);
            }
        } 
        // Otherwise: content-desc > text > hint > resource-id > class > xpath
        else {
            if (!text.isEmpty()) {
                return roleSemantic(role, text);
            }
            if (!hint.isEmpty()) {
                return roleSemantic(role, hint);
            }
        }

        String resourceId = trim(widget.get(AndroidTags.AndroidResourceId, ""));
        if (!resourceId.isEmpty()) {
            return roleSemantic(role, resourceId);
        }

        String className = trim(widget.get(AndroidTags.AndroidClassName, ""));
        if (!className.isEmpty()) {
            return semanticRole(widget);
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

    private static String semanticRole(Widget widget) {
        String className = trim(widget.get(AndroidTags.AndroidClassName, ""));
        if (className.isEmpty()) {
            return "widget";
        }

        int separatorIndex = className.lastIndexOf('.');
        String simpleName = separatorIndex >= 0 ? className.substring(separatorIndex + 1) : className;
        return normalize(simpleName);
    }

    private static String roleSemantic(String role, String semanticValue) {
        String semantic = normalize(semanticValue);
        if (semantic.isEmpty()) {
            return role;
        }
        return role + "_" + semantic;
    }

    private static String normalize(String value) {
        String trimmed = trim(value).toLowerCase();
        if (trimmed.isEmpty()) {
            return "";
        }

        String normalized = trimmed.replaceAll("\\s+", "_");
        normalized = normalized.replaceAll("[\\?\\*\\u2022\\u2023\\u25e6\\u2043\\u2219\\u25cf\\ufffd]{2,}", "??????");
        normalized = normalized.replaceAll("[^a-z0-9_./?-]", "");
        normalized = normalized.replaceAll("_+", "_");
        return normalized;
    }
}
