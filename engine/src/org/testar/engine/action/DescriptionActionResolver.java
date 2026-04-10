/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Role;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

/**
 * Default action resolver that orders and matches actions using {@link Tags#Desc}.
 * Matchers can replace this with locator-based or platform-specific logic.
 */
public final class DescriptionActionResolver implements ActionResolver {

    @Override
    public ResolvedAction resolve(Iterable<Action> actions, List<String> arguments) {
        List<Action> orderedActions = orderedActions(actions);
        String selector = argumentAt(arguments, 0);
        if (selector == null) {
            throw new IllegalArgumentException("executeAction requires an action selector");
        }

        String mode = selector.toLowerCase(Locale.ROOT);
        String semanticText = argumentAt(arguments, 1);
        if (semanticText == null || semanticText.isBlank()) {
            throw new IllegalArgumentException("executeAction " + mode + " requires semantic text");
        }

        switch (mode) {
            case "click":
                return findSemanticAction(orderedActions, semanticText, false)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "no click action matched semantic text: " + semanticText
                        ));
            case "type":
                String inputText = joinArguments(arguments, 2);
                if (inputText == null || inputText.isBlank()) {
                    throw new IllegalArgumentException("executeAction type requires input text");
                }
                ResolvedAction matchedAction = findSemanticAction(orderedActions, semanticText, true)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "no type action matched semantic text: " + semanticText
                        ));
                return new ResolvedAction(buildTypedAction(matchedAction.action(), inputText));
            default:
                throw new IllegalArgumentException("Unsupported executeAction mode: " + selector);
        }
    }

    private List<Action> orderedActions(Iterable<Action> actions) {
        List<Action> orderedActions = new ArrayList<>();
        for (Action action : actions) {
            orderedActions.add(action);
        }
        orderedActions.sort(Comparator.comparing(action -> action.get(Tags.Desc, action.toString())));
        return orderedActions;
    }

    private Optional<ResolvedAction> findSemanticAction(List<Action> orderedActions,
                                                        String semanticText,
                                                        boolean typeAction) {
        String normalizedSemanticText = normalizeText(semanticText);
        for (int index = 0; index < orderedActions.size(); index++) {
            Action action = orderedActions.get(index);
            if (typeAction ? isTypeAction(action) : isClickAction(action)) {
                if (normalizeText(action.get(Tags.Desc, action.toString())).contains(normalizedSemanticText)) {
                    return Optional.of(new ResolvedAction(action));
                }
            }
        }
        return Optional.empty();
    }

    private Action buildTypedAction(Action templateAction, String inputText) {
        Widget originWidget = templateAction.get(Tags.OriginWidget, null);
        if (isWebdriverTypeAction(templateAction)) {
            applyWebdriverInputText(templateAction, inputText);
            return templateAction;
        }

        Role role = templateAction.get(Tags.Role, null);
        if (role == ActionRoles.ClickTypeInto) {
            if (originWidget == null) {
                throw new IllegalArgumentException("Matched type action does not have an origin widget");
            }
            return new AnnotatingActionCompiler().clickTypeInto(originWidget, inputText, true);
        }

        throw new IllegalArgumentException(
                "Matched type action cannot be rebuilt with custom text: "
                        + templateAction.get(Tags.Desc, templateAction.toString())
        );
    }

    private boolean isWebdriverTypeAction(Action action) {
        String className = action.getClass().getName();
        return "org.testar.webdriver.action.WdRemoteTypeAction".equals(className)
                || "org.testar.webdriver.action.WdRemoteScrollTypeAction".equals(className);
    }

    private void applyWebdriverInputText(Action action, String inputText) {
        try {
            action.getClass().getMethod("setKeys", CharSequence.class).invoke(action, inputText);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                    "Matched WebDriver type action cannot be rebuilt with custom text",
                    exception
            );
        }
    }

    private boolean isClickAction(Action action) {
        Role role = action.get(Tags.Role, null);
        String roleName = role == null ? "" : role.toString().toLowerCase(Locale.ROOT);
        return roleName.contains("click") && !roleName.contains("type");
    }

    private boolean isTypeAction(Action action) {
        Role role = action.get(Tags.Role, null);
        String roleName = role == null ? "" : role.toString().toLowerCase(Locale.ROOT);
        return roleName.contains("type");
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }

    private String joinArguments(List<String> arguments, int startIndex) {
        if (startIndex < 0 || startIndex >= arguments.size()) {
            return null;
        }
        return String.join(" ", arguments.subList(startIndex, arguments.size()));
    }

    private String argumentAt(List<String> arguments, int index) {
        return index >= 0 && index < arguments.size() ? arguments.get(index) : null;
    }
}
