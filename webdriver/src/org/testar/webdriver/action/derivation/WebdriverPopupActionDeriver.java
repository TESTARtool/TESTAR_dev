/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.webdriver.action.WdRemoteScrollClickAction;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;

/**
 * Derives click actions for visible widgets matching configured widget attributes.
 */
public final class WebdriverPopupActionDeriver implements ActionDeriver {

    private final List<AttributeSelector> selectors;

    public WebdriverPopupActionDeriver(List<String> configuredSelectors) {
        Assert.notNull(configuredSelectors);
        selectors = parseSelectors(configuredSelectors);
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state);
        Set<Action> actions = new LinkedHashSet<>();

        for (Widget widget : state) {
            if (!(widget instanceof WdWidget)
                    || widget.get(WdTags.WebIsHidden, false)
                    || !widget.get(WdTags.WebIsDisplayed, true)
                    || !widget.get(WdTags.WebIsEnabled, true)) {
                continue;
            }

            Map<String, String> attributes = widget.get(WdTags.WebAttributeMap, null);
            if (matchesAnySelector(attributes)) {
                actions.add(new WdRemoteScrollClickAction((WdWidget) widget));
            }
        }

        return Collections.unmodifiableSet(actions);
    }

    private boolean matchesAnySelector(Map<String, String> attributes) {
        if (attributes == null || selectors.isEmpty()) {
            return false;
        }

        for (AttributeSelector selector : selectors) {
            if (selector.value.equals(attributes.get(selector.name))) {
                return true;
            }
        }
        return false;
    }

    private List<AttributeSelector> parseSelectors(List<String> configuredSelectors) {
        List<AttributeSelector> parsedSelectors = new ArrayList<>();
        for (String configuredSelector : configuredSelectors) {
            String selector = configuredSelector == null ? "" : configuredSelector.trim();
            if (selector.isEmpty()) {
                continue;
            }

            int separator = selector.indexOf('=');
            if (separator <= 0 || separator == selector.length() - 1) {
                throw new IllegalArgumentException(
                        "Invalid WebForcedPopupClickAttributes entry '" + selector + "'. Expected attribute=value."
                );
            }

            parsedSelectors.add(new AttributeSelector(
                    selector.substring(0, separator).trim(),
                    selector.substring(separator + 1).trim()
            ));
        }
        return Collections.unmodifiableList(parsedSelectors);
    }

    private static final class AttributeSelector {
        private final String name;
        private final String value;

        private AttributeSelector(String name, String value) {
            if (name.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException(
                        "Popup click selectors require a non-empty attribute and value. Expected attribute=value."
                );
            }
            this.name = name;
            this.value = value;
        }
    }
}
