/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.resolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Role;
import org.testar.core.state.State;
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
        String mode = normalizedMode(arguments);
        String semanticText = requiredSemanticText(arguments, mode);

        switch (mode) {
            case "click":
                return new ResolvedAction(resolveClickAction(orderedActions, semanticText));
            case "type":
                return new ResolvedAction(resolveTypeAction(orderedActions, semanticText, requiredInput(arguments, mode)));
            case "select":
                return new ResolvedAction(resolveSelectAction(orderedActions, semanticText, requiredInput(arguments, mode)));
            default:
                throw new IllegalArgumentException("Unsupported executeAction mode: " + mode);
        }
    }

    private String normalizedMode(List<String> arguments) {
        String selector = argumentAt(arguments, 0);
        if (selector == null) {
            throw new IllegalArgumentException("executeAction requires an action selector");
        }
        return selector.toLowerCase(Locale.ROOT);
    }

    private String requiredSemanticText(List<String> arguments, String mode) {
        String semanticText = argumentAt(arguments, 1);
        if (semanticText == null || semanticText.isBlank()) {
            throw new IllegalArgumentException("executeAction " + mode + " requires semantic text");
        }
        return semanticText;
    }

    private String requiredInput(List<String> arguments, String mode) {
        String input = joinArguments(arguments, 2);
        if (input == null || input.isBlank()) {
            if ("type".equals(mode)) {
                throw new IllegalArgumentException("executeAction type requires input text");
            }
            throw new IllegalArgumentException("executeAction " + mode + " requires a value");
        }
        return input;
    }

    private List<Action> orderedActions(Iterable<Action> actions) {
        List<Action> orderedActions = new ArrayList<>();
        for (Action action : actions) {
            orderedActions.add(action);
        }
        orderedActions.sort(Comparator.comparing(action -> action.get(Tags.Desc, action.toString())));
        return orderedActions;
    }

    private Action resolveClickAction(List<Action> orderedActions, String semanticText) {
        return findRequiredAction(
                orderedActions,
                semanticText,
                this::isClickAction,
                "no click action matched semantic text: " + semanticText
        );
    }

    private Action resolveTypeAction(List<Action> orderedActions, String semanticText, String inputText) {
        Action templateAction = findRequiredAction(
                orderedActions,
                semanticText,
                this::isTypeAction,
                "no type action matched semantic text: " + semanticText
        );
        return buildTypedAction(templateAction, inputText);
    }

    private Action resolveSelectAction(List<Action> orderedActions, String semanticText, String selectedValue) {
        Action templateAction = findRequiredAction(
                orderedActions,
                semanticText,
                this::isSelectAction,
                "no select action matched semantic text: " + semanticText
        );
        return buildSelectAction(templateAction, selectedValue);
    }

    private Action findRequiredAction(List<Action> orderedActions,
                                      String semanticText,
                                      Predicate<Action> actionFilter,
                                      String errorMessage) {
        return findSemanticAction(orderedActions, semanticText, actionFilter)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private Optional<Action> findSemanticAction(List<Action> orderedActions,
                                                String semanticText,
                                                Predicate<Action> actionFilter) {
        String normalizedSemanticText = normalizeText(semanticText);
        Optional<Action> exactMatch = findMatchingAction(
                orderedActions,
                actionFilter,
                action -> normalizedDescription(action).equals(normalizedSemanticText)
        );
        if (exactMatch.isPresent()) {
            return exactMatch;
        }

        return findMatchingAction(
                orderedActions,
                actionFilter,
                action -> normalizedDescription(action).contains(normalizedSemanticText)
        );
    }

    private Optional<Action> findMatchingAction(List<Action> orderedActions,
                                                Predicate<Action> actionFilter,
                                                Predicate<Action> semanticMatcher) {
        for (Action action : orderedActions) {
            if (actionFilter.test(action) && semanticMatcher.test(action)) {
                return Optional.of(action);
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

        Action rebuiltPlatformAction = rebuildTypedActionWithSameClass(templateAction, originWidget, inputText);
        if (rebuiltPlatformAction != null) {
            return withCopiedIdentity(templateAction, rebuiltPlatformAction);
        }

        Role role = templateAction.get(Tags.Role, null);
        if (role == ActionRoles.ClickTypeInto) {
            if (originWidget == null) {
                throw new IllegalArgumentException("Matched type action does not have an origin widget");
            }
            return withCopiedIdentity(
                    templateAction,
                    new AnnotatingActionCompiler().clickTypeInto(originWidget, inputText, true)
            );
        }

        throw new IllegalArgumentException(
                "Matched type action cannot be rebuilt with custom text: "
                        + templateAction.get(Tags.Desc, templateAction.toString())
        );
    }

    private Action rebuildTypedActionWithSameClass(Action templateAction, Widget originWidget, String inputText) {
        if (originWidget == null) {
            return null;
        }

        Widget rootWidget = originWidget.root();
        if (!(rootWidget instanceof State)) {
            return null;
        }

        try {
            return (Action) templateAction.getClass()
                    .getConstructor(State.class, Widget.class, String.class)
                    .newInstance((State) rootWidget, originWidget, inputText);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private Action buildSelectAction(Action templateAction, String selectedValue) {
        Widget originWidget = templateAction.get(Tags.OriginWidget, null);
        if (originWidget == null) {
            throw new IllegalArgumentException("Matched select action does not have an origin widget");
        }

        Action rebuiltAction = invokeStaticActionFactory(
                "org.testar.webdriver.action.WebdriverSelectListSupport",
                "createActionForInput",
                new Class<?>[] { Widget.class, String.class },
                new Object[] { originWidget, selectedValue },
                "Matched select action cannot be rebuilt without WebDriver select support"
        );
        if (rebuiltAction != null) {
            return withCopiedIdentity(templateAction, rebuiltAction);
        }

        throw new IllegalArgumentException("Matched select action cannot be rebuilt due to missing target");
    }

    private Action invokeStaticActionFactory(String className,
                                             String methodName,
                                             Class<?>[] parameterTypes,
                                             Object[] arguments,
                                             String errorMessage) {
        try {
            Object action = Class.forName(className)
                    .getMethod(methodName, parameterTypes)
                    .invoke(null, arguments);
            if (action instanceof Action) {
                return (Action) action;
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(errorMessage, exception);
        }
        return null;
    }

    private Action withCopiedIdentity(Action sourceAction, Action targetAction) {
        String abstractId = sourceAction.get(Tags.AbstractID, "");
        if (!abstractId.isBlank()) {
            targetAction.set(Tags.AbstractID, abstractId);
        }

        String concreteId = sourceAction.get(Tags.ConcreteID, "");
        if (!concreteId.isBlank()) {
            targetAction.set(Tags.ConcreteID, concreteId);
        }

        return targetAction;
    }

    private boolean isWebdriverTypeAction(Action action) {
        String className = action.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("remote") && className.contains("type");
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
        String roleName = roleNameOf(action);
        return roleName.contains("click") && !roleName.contains("type");
    }

    private boolean isTypeAction(Action action) {
        return roleNameOf(action).contains("type");
    }

    private boolean isSelectAction(Action action) {
        return roleNameOf(action).contains("select");
    }

    private String roleNameOf(Action action) {
        Role role = action.get(Tags.Role, null);
        return role == null ? "" : role.toString().toLowerCase(Locale.ROOT);
    }

    private String normalizedDescription(Action action) {
        return normalizeText(action.get(Tags.Desc, action.toString()));
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = text.toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[\\?\\*\\u2022\\u2023\\u25e6\\u2043\\u2219\\u25cf\\ufffd]{2,}", " passwordmask ");
        return normalized;
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
