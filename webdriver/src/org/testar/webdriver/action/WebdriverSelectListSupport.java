/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.core.action.Action;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverSelectListSupport {

    private static final Pattern OPTION_PATTERN = Pattern.compile(
            "<option([^>]*)>(.*?)</option>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern VALUE_PATTERN = Pattern.compile(
            "value\\s*=\\s*\"([^\"]*)\"",
            Pattern.CASE_INSENSITIVE
    );

    private WebdriverSelectListSupport() {
    }

    public static List<SelectOption> extractOptions(Widget widget) {
        String innerHtml = widget.get(WdTags.WebInnerHTML, "");
        Matcher optionMatcher = OPTION_PATTERN.matcher(innerHtml);
        List<SelectOption> options = new ArrayList<>();
        while (optionMatcher.find()) {
            String optionAttributes = optionMatcher.group(1);
            String optionLabel = sanitizeOptionText(optionMatcher.group(2));
            String optionValue = extractOptionValue(optionAttributes, optionLabel);
            options.add(new SelectOption(optionLabel, optionValue));
        }
        return options;
    }

    public static Action createSelectAction(Widget widget) {
        SelectTarget selectTarget = resolveTarget(widget);
        if (selectTarget == null) {
            return null;
        }

        List<SelectOption> options = extractOptions(widget);
        String selectedValue = options.isEmpty() ? "" : options.get(0).value();
        WdSelectListAction action = new WdSelectListAction(
                selectTarget.target(),
                selectedValue,
                widget,
                selectTarget.targetMethod()
        );
        action.set(Tags.Desc, describeOptions(widget, options));
        return action;
    }

    public static Action createActionForInput(Widget widget, String input) {
        SelectTarget selectTarget = resolveTarget(widget);
        if (selectTarget == null) {
            return null;
        }

        String selectedValue = mapInputToOptionValue(widget, input);
        return new WdSelectListAction(
                selectTarget.target(),
                selectedValue,
                widget,
                selectTarget.targetMethod()
        );
    }

    public static String describeOptions(Widget widget, List<SelectOption> options) {
        String widgetDescription = widget.get(Tags.Desc, widget.toString());
        StringBuilder builder = new StringBuilder();
        builder.append("Set ComboBox '").append(widgetDescription).append("' to one of the following values: ");
        for (SelectOption option : options) {
            builder.append(option.label()).append(",");
        }
        return builder.toString();
    }

    public static String mapInputToOptionValue(Widget widget, String input) {
        String normalizedInput = normalize(input);
        for (SelectOption option : extractOptions(widget)) {
            if (normalize(option.label()).equals(normalizedInput)
                    || normalize(option.value()).equals(normalizedInput)) {
                return option.value();
            }
        }
        return input;
    }

    private static SelectTarget resolveTarget(Widget widget) {
        String target = widget.get(WdTags.WebId, "");
        if (!target.isBlank()) {
            return new SelectTarget(target, WdSelectListAction.JsTargetMethod.ID);
        }

        target = widget.get(WdTags.WebName, "");
        if (!target.isBlank()) {
            return new SelectTarget(target, WdSelectListAction.JsTargetMethod.NAME);
        }

        return null;
    }

    private static String extractOptionValue(String optionAttributes, String optionLabel) {
        Matcher valueMatcher = VALUE_PATTERN.matcher(optionAttributes == null ? "" : optionAttributes);
        if (valueMatcher.find()) {
            return preserveOptionValue(valueMatcher.group(1));
        }
        return optionLabel;
    }

    private static String sanitizeOptionText(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replaceAll("<[^>]*>", " ");
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        return sanitized;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String preserveOptionValue(String value) {
        return value == null ? "" : value.trim();
    }

    public record SelectOption(String label, String value) {
    }

    private record SelectTarget(String target, WdSelectListAction.JsTargetMethod targetMethod) {
    }
}
